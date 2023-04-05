package com.picpay.quickstart.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun circuitBreakerConfig() = CircuitBreakerConfig.custom()
        .slidingWindowSize(2)
        .minimumNumberOfCalls(2)
        .enableAutomaticTransitionFromOpenToHalfOpen()
        .waitDurationInOpenState(30.seconds.toJavaDuration())
        .build()

    @Primary
    @Bean("circuitBreakerDefault")
    fun circuitBreakerDefault(config: CircuitBreakerConfig) = CircuitBreaker.of("default", config).apply {
        eventPublisher.onStateTransition {
            if (it.stateTransition.toState == CircuitBreaker.State.OPEN) {
                println("=== call event to disable provider ===")
            }

            if (it.stateTransition.toState == CircuitBreaker.State.HALF_OPEN) {
                println("=== call event to enable provider ===")
                this.transitionToClosedState()
            }
        }
    }

    @Bean("circuitBreakerCached")
    fun circuitBreakerCached(config: CircuitBreakerConfig) = CircuitBreaker.of("cached", config)
}
