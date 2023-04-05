package com.picpay.quickstart.circuitbreaker

import br.com.guiabolso.events.json.MapperHolder
import com.picpay.quickstart.ProviderService
import com.picpay.quickstart.circuitbreaker.CachedCircuitBreakerControl.Companion.CIRCUIT_BREAKER_METADATA_PREFIX
import com.picpay.quickstart.config.propertiesconfig.ConfigService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub

@Component
class CachedCircuitBreakerControl(
    val circuits: Map<String, CircuitBreaker>,
    val providerService: ProviderService,
    val configService: ConfigService
) {

    final val redisPool: JedisPooled by lazy {
        JedisPooled(
            configService.getRequiredString("redis.url"),
            configService.getRequiredInt("redis.port")
        )
    }

    final val redisSubscriberPool: JedisPooled by lazy {
        JedisPooled(
            configService.getRequiredString("redis.url"),
            configService.getRequiredInt("redis.port")
        )
    }

    init {
        subscribeToChanges()
        circuits.forEach { _, circuit ->
            circuit.subscribeOnCircuitError()
            circuit.subscribeOnCircuitSuccess()
            circuit.subscribeOnStateTransition()
        }
    }

    private fun subscribeToChanges() {
        Executors.newSingleThreadExecutor().execute {
            redisSubscriberPool.subscribe(
                Subscription(),
                CIRCUIT_BREAKER_CHANNEL
            )
        }
    }

    private fun CircuitBreaker.subscribeOnCircuitError() {
        eventPublisher.onError {
            val requests = redisPool.fetchMetadataFor(this).updateRequestAndPush(false)

            val errorRate = requests.calculateErrorRate()
            log.info("onError: metadata=$requests, errorRate=$errorRate" +
                "threshold >= ${circuitBreakerConfig.failureRateThreshold}")

            if (errorRate >= circuitBreakerConfig.failureRateThreshold) {
                redisPool.publish(CIRCUIT_BREAKER_CHANNEL, this.name)
            }
        }
    }

    private fun CircuitBreaker.subscribeOnCircuitSuccess() {
        eventPublisher.onSuccess {
            val requests = redisPool.fetchMetadataFor(this).updateRequestAndPush(true)

            if (state != CircuitBreaker.State.CLOSED) {
                val errorRate = requests.calculateErrorRate()
                log.info("onSuccess: metadata=$requests, errorRate=$errorRate" +
                    "threshold >= ${circuitBreakerConfig.failureRateThreshold}")

                if (errorRate < circuitBreakerConfig.failureRateThreshold) {
                    redisPool.publish(CIRCUIT_BREAKER_CHANNEL, this.name)
                }
            }
        }
    }

    private fun CircuitBreaker.subscribeOnStateTransition() {
        eventPublisher.onStateTransition {

            when (it.stateTransition) {
                CircuitBreaker.StateTransition.CLOSED_TO_FORCED_OPEN -> this.transitionToOpenState()
                CircuitBreaker.StateTransition.CLOSED_TO_OPEN -> disableProviderAndSaveState(this)
                CircuitBreaker.StateTransition.OPEN_TO_HALF_OPEN -> providerService.enable(name)
                CircuitBreaker.StateTransition.HALF_OPEN_TO_OPEN -> providerService.disable(name)
                CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED -> enableProviderAndSaveState(this)

                // Precisa verificar se existe outra transição que deveria ser validada
                // Ou se precisaria validar apenas pra qual estado está INDO sem precisar ver de qual estado está VINDO
                else -> log.info("The ignored state transition: ${it.stateTransition}")
            }
        }
    }

    private fun disableProviderAndSaveState(circuitBreaker: CircuitBreaker) {
        providerService.disable(circuitBreaker.name)
        cacheState(circuitBreaker)
    }

    private fun enableProviderAndSaveState(circuitBreaker: CircuitBreaker) {
        providerService.enable(circuitBreaker.name)
        cacheState(circuitBreaker)
    }

    private fun cacheState(circuitBreaker: CircuitBreaker) {
        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO CACHE TO KEEP IT STATEFUL ===")
        redisPool.fetchMetadataFor(circuitBreaker).updateRequestAndPush(false)
    }

    private fun JedisPooled.fetchMetadataFor(circuitBreaker: CircuitBreaker) =
        get(circuitBreaker.cacheKeyName).asRequestMetadata(circuitBreaker)

    private fun CircuitBreakeCachedMetadata.updateRequestAndPush(success: Boolean) =
        apply { update(success) }.also { redisPool.set(cacheKeyName, it.toJson()) }

    private fun CircuitBreakeCachedMetadata.updateStateAndPush(state: CircuitBreaker.State) =
        apply { this.state = state }.also { redisPool.set(cacheKeyName, it.toJson()) }

    private fun CircuitBreaker.errorRateReachedLimit(): Boolean {
        val failuredThreshold = circuitBreakerConfig.failureRateThreshold
        val requests = redisPool.fetchMetadataFor(this)

        val errorRate = requests.calculateErrorRate()

        log.info("shared result: metadata=$requests, errorRate=$errorRate, threshold >= ${failuredThreshold}")
        return errorRate >= failuredThreshold
    }

    companion object {
        const val CIRCUIT_BREAKER_CHANNEL = "circuit_breaker_state"
        const val CIRCUIT_BREAKER_METADATA_PREFIX = "circuit_breaker_metadata"
        val log = LoggerFactory.getLogger(CachedCircuitBreakerControl::class.java)
    }

    inner class Subscription : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            log.info("$channel $message")
            if (message == "ping") {
                this.ping()
            }

            val circuit = circuits[message] ?: return
            if (circuit.isClosed() && circuit.errorRateReachedLimit()) {
                log.info("=== CIRCUIT BREAKER IS NOW OPEN ===")
                circuit.transitionToForcedOpenState()
            } else {
                log.info("=== CIRCUIT BREAKER IS NOW CLOSED ===")
                circuit.transitionToClosedState()
            }
        }

        override fun onSubscribe(channel: String, subscribedChannels: Int) {

            if (channel != CIRCUIT_BREAKER_CHANNEL) return

            circuits.forEach { _, circuit ->
                val metadata = redisPool.fetchMetadataFor(circuit)

                if (metadata.state == CircuitBreaker.State.OPEN) {
                    circuit.transitionToForcedOpenState()
                }
            }
        }
    }
}

fun CircuitBreaker.isClosed() = state == CircuitBreaker.State.CLOSED

val CircuitBreaker.cacheKeyName get() = "${CIRCUIT_BREAKER_METADATA_PREFIX}_${name}"

fun String?.asRequestMetadata(circuitBreaker: CircuitBreaker): CircuitBreakeCachedMetadata {
    return if (this == null) CircuitBreakeCachedMetadata(
        maxSize = circuitBreaker.circuitBreakerConfig.slidingWindowSize,
        cacheKeyName = circuitBreaker.cacheKeyName
    )
    else MapperHolder.mapper.fromJson(this, CircuitBreakeCachedMetadata::class.java)
}

fun CircuitBreakeCachedMetadata.toJson(): String = MapperHolder.mapper.toJson(this)
