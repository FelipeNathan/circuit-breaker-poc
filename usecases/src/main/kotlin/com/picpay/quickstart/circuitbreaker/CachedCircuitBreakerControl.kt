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
class CachedCircuitBreakerControl(
    @Qualifier("circuitBreakerCached")
    val circuitBreakerCached: CircuitBreaker,
    val providerService: ProviderService
) {

    final val redisPool: JedisPooled by lazy {
        JedisPooled(REDIS_URL, REDIS_PORT)
    }

    init {
        circuitBreakerCached.eventPublisher.onStateTransition {

            when (it.stateTransition) {
                CircuitBreaker.StateTransition.CLOSED_TO_OPEN -> openCircuit()
                CircuitBreaker.StateTransition.OPEN_TO_HALF_OPEN -> providerService.enable()
                CircuitBreaker.StateTransition.HALF_OPEN_TO_CLOSED -> closeCircuit()

                else -> log.info("The ignored transition: ${it.stateTransition}")
            }
        }

        Executors.newSingleThreadExecutor().execute {
            redisPool.subscribe(
                Subscription(),
                CIRCUIT_BREAKER_CHANNEL
            )
        }
    }

    private fun openCircuit() {
        providerService.disable()
        publishAndCacheState(CircuitBreaker.State.OPEN)
    }

    private fun closeCircuit() {
        providerService.enable()
        publishAndCacheState(CircuitBreaker.State.CLOSED)
    }

    private fun publishAndCacheState(state: CircuitBreaker.State) {
        log.info("=== PUBLISH CIRCUIT BREAKER STATE TO PODS FORCE $state ===")
        redisPool.publish(CIRCUIT_BREAKER_CHANNEL, state.name)
        redisPool.set(CIRCUIT_BREAKER_CHANNEL, state.name)
    }

    companion object {
        const val REDIS_URL = "localhost"
        const val REDIS_PORT = 6379
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
