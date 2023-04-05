package com.picpay.quickstart.circuitbreaker

import com.picpay.quickstart.ProviderService
import com.picpay.quickstart.config.propertiesconfig.ConfigService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.Executors

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

    init {

        circuitBreakerCached.eventPublisher.onStateTransition {

            when (it.stateTransition) {
                CircuitBreaker.StateTransition.CLOSED_TO_OPEN -> disableProviderAndNotifyOpenState()
                CircuitBreaker.StateTransition.OPEN_TO_HALF_OPEN -> providerService.enable()
                CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED -> enableProviderAndNotifyClosedState()
                CircuitBreaker.StateTransition.CLOSED_TO_FORCED_OPEN -> circuitBreakerCached.transitionToOpenState()

                // Precisa verificar se existe outra transição que deveria ser validada
                // Ou se precisaria validar apenas pra qual estado está INDO sem precisar ver de qual estado está VINDO
                else -> log.info("The ignored state transition: ${it.stateTransition}")
            }
        }

        Executors.newSingleThreadExecutor().execute {
            redisPool.subscribe(
                Subscription(),
                CIRCUIT_BREAKER_CHANNEL
            )
        }
    }

    private fun disableProviderAndNotifyOpenState() {
        providerService.disable()
        publishAndCacheState(CircuitBreaker.State.OPEN)
    }

    private fun enableProviderAndNotifyClosedState() {
        providerService.enable()
        publishAndCacheState(CircuitBreaker.State.CLOSED)
    }

    private fun publishAndCacheState(state: CircuitBreaker.State) {
        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO PODS FORCE $state ===")
        redisPool.publish(CIRCUIT_BREAKER_CHANNEL, state.name)

        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO CACHE TO KEEP IT STATEFUL ===")
        redisPool.set(CIRCUIT_BREAKER_CHANNEL, state.name)
    }

    companion object {
        const val CIRCUIT_BREAKER_CHANNEL = "circuit_breaker_state"
        val log = LoggerFactory.getLogger(CachedCircuitBreakerControl::class.java)
    }

    inner class Subscription : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            log.info("$channel $message")
            if (message == "ping") {
                this.ping()
            }

            val state = try {
                CircuitBreaker.State.valueOf(message)
            } catch (ignored: Exception) {
                return
            }

            if (circuitBreakerCached.state == state) {
                return
            }

            when (state) {
                CircuitBreaker.State.OPEN -> {
                    log.info("=== CIRCUIT BREAKER IS NOW OPEN ===")
                    circuitBreakerCached.transitionToForcedOpenState()
                }

                CircuitBreaker.State.CLOSED -> {
                    log.info("=== CIRCUIT BREAKER IS NOW CLOSED ===")
                    circuitBreakerCached.transitionToClosedState()
                }

                else -> {}
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
