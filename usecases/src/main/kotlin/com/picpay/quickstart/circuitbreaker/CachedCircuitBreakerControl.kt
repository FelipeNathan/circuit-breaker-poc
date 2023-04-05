package com.picpay.quickstart.circuitbreaker

import com.picpay.quickstart.ProviderService
import com.picpay.quickstart.config.propertiesconfig.ConfigService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub

@Component
class CachedCircuitBreakerControl(
    @Qualifier("circuitBreakerCached")
    val circuitBreakerCached: CircuitBreaker,
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

        Executors.newSingleThreadExecutor().execute {
            redisSubscriberPool.subscribe(
                Subscription(),
                CIRCUIT_BREAKER_CHANNEL
            )
        }

        circuitBreakerCached.eventPublisher.onError {
            val errors = redisPool.incr(ERROR_BUCKET)
            val success = redisPool.get(SUCCESS_BUCKET)?.toLong() ?: 0
            val errorRate = errors.calculateErrorRate(success)

            log.info("onError: errors=$errors, success=$success, errorRate=$errorRate = " +
                "threshold >= ${circuitBreakerCached.circuitBreakerConfig.failureRateThreshold}")

            if (errorRate >= circuitBreakerCached.circuitBreakerConfig.failureRateThreshold) {
                redisPool.publish(CIRCUIT_BREAKER_CHANNEL, "update state")
            }
        }

        circuitBreakerCached.eventPublisher.onSuccess {
            val success = redisPool.incr(SUCCESS_BUCKET)
            val errors = redisPool.get(ERROR_BUCKET)?.toLong() ?: 0

            log.info("onSuccess: ${circuitBreakerCached.state}")
            if (circuitBreakerCached.state != CircuitBreaker.State.CLOSED) {
                val errorRate = errors.calculateErrorRate(success)

                log.info("onSuccess: errors=$errors, success=$success, errorRate=$errorRate = " +
                    "threshold >= ${circuitBreakerCached.circuitBreakerConfig.failureRateThreshold}")

                if (errorRate < circuitBreakerCached.circuitBreakerConfig.failureRateThreshold) {
                    redisPool.del(SUCCESS_BUCKET)
                    redisPool.del(ERROR_BUCKET)
                    redisPool.publish(CIRCUIT_BREAKER_CHANNEL, "update state")
                }
            }
        }

        circuitBreakerCached.eventPublisher.onStateTransition {

            when (it.stateTransition) {
                CircuitBreaker.StateTransition.CLOSED_TO_FORCED_OPEN -> circuitBreakerCached.transitionToOpenState()
                CircuitBreaker.StateTransition.CLOSED_TO_OPEN -> disableProviderAndSaveState()
                CircuitBreaker.StateTransition.OPEN_TO_HALF_OPEN -> providerService.enable()
                CircuitBreaker.StateTransition.HALF_OPEN_TO_OPEN -> providerService.disable()
                CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED -> enableProviderAndSaveState()

                // Precisa verificar se existe outra transição que deveria ser validada
                // Ou se precisaria validar apenas pra qual estado está INDO sem precisar ver de qual estado está VINDO
                else -> log.info("The ignored state transition: ${it.stateTransition}")
            }
        }
    }

    private fun disableProviderAndSaveState() {
        providerService.disable()
        cacheState(CircuitBreaker.State.OPEN)
    }

    private fun enableProviderAndSaveState() {
        providerService.enable()
        cacheState(CircuitBreaker.State.CLOSED)
    }

    private fun cacheState(state: CircuitBreaker.State) {
        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO CACHE TO KEEP IT STATEFUL ===")
        redisPool.set(CIRCUIT_BREAKER_CHANNEL, state.name)
    }

    private fun CircuitBreaker.errorRateReachedLimit(): Boolean {
        val failuredThreshold = circuitBreakerCached.circuitBreakerConfig.failureRateThreshold
        val success = redisPool.get(SUCCESS_BUCKET)?.toLong() ?: 0
        val errors = redisPool.get(ERROR_BUCKET)?.toLong() ?: 0
        val errorRate = errors.calculateErrorRate(success)

        log.info("Shared result: errors=$errors, success=$success, errorRate=$errorRate = " +
            "threshold >= ${circuitBreakerCached.circuitBreakerConfig.failureRateThreshold}")
        return errorRate >= failuredThreshold
    }

    private fun Long.calculateErrorRate(success: Long) = (this.toDouble() / (this + success.toDouble())) * 100

    companion object {
        const val CIRCUIT_BREAKER_CHANNEL = "circuit_breaker_state"
        const val SUCCESS_BUCKET = "circuit_breaker_success"
        const val ERROR_BUCKET = "circuit_breaker_error"

        val log = LoggerFactory.getLogger(CachedCircuitBreakerControl::class.java)
    }

    inner class Subscription : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            log.info("$channel $message")
            if (message == "ping") {
                this.ping()
            }

            if (circuitBreakerCached.errorRateReachedLimit()) {
                log.info("=== CIRCUIT BREAKER IS NOW OPEN ===")
                circuitBreakerCached.transitionToForcedOpenState()
            } else {
                log.info("=== CIRCUIT BREAKER IS NOW CLOSED ===")
                circuitBreakerCached.transitionToClosedState()
            }
        }

        override fun onSubscribe(channel: String, subscribedChannels: Int) {
            val state = redisPool.get(CIRCUIT_BREAKER_CHANNEL) ?: return

            if (state == CircuitBreaker.State.OPEN.name) {
                circuitBreakerCached.transitionToForcedOpenState()
            }
        }
    }
}
