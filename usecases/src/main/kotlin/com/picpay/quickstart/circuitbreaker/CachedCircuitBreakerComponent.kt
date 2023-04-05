package com.picpay.quickstart.circuitbreaker

import com.picpay.quickstart.ProviderService
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub

@Component
class CachedCircuitBreakerComponent(
    @Qualifier("circuitBreakerCached")
    circuitBreakerCached: CircuitBreaker,
    providerService: ProviderService
) {

    final val redisPool: JedisPooled by lazy {
        JedisPooled(REDIS_URL, REDIS_PORT)
    }

    init {
        circuitBreakerCached.eventPublisher.onStateTransition {

            when (it.stateTransition) {
                CircuitBreaker.StateTransition.CLOSED_TO_OPEN -> {
                    providerService.disable()
                    publishState(it.stateTransition.toState)
                }

                CircuitBreaker.StateTransition.OPEN_TO_HALF_OPEN -> providerService.enable()

                CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED -> {
                    providerService.enable()
                    publishState(it.stateTransition.toState)
                }

                else -> {
                    log.info("The ignored transition: ${it.stateTransition}")
                }
            }
        }

        Executors.newSingleThreadExecutor().execute {
            redisPool.subscribe(
                Subscription(circuitBreakerCached),
                CIRCUIT_BREAKER_CHANNEL
            )
        }
    }

    private fun publishState(state: CircuitBreaker.State) {
        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO PODS FORCE $state ===")
        redisPool.publish(CIRCUIT_BREAKER_CHANNEL, state.name)
    }

    companion object {
        const val REDIS_URL = "localhost"
        const val REDIS_PORT = 6379
        const val CIRCUIT_BREAKER_CHANNEL = "circuit_breaker_state"
        val log = LoggerFactory.getLogger(CachedCircuitBreakerComponent::class.java)
    }

    class Subscription(private val circuitBreakerCached: CircuitBreaker) : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            if (message == "ping") {
                this.ping()
            }

            val state = CircuitBreaker.State.valueOf(message)

            log.info("$channel $message")
            if (circuitBreakerCached.state == state) {
                return
            }

            when (CircuitBreaker.State.valueOf(message)) {
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

        override fun onSubscribe(channel: String?, subscribedChannels: Int) {
            log.info("Me inscrevi no $channel e ae?")
        }
    }
}
