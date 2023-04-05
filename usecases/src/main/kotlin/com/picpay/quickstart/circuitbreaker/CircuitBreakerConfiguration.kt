package com.picpay.quickstart.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun circuitBreakerConfig() = CircuitBreakerConfig.custom()
        .slidingWindowSize(4)
        .enableAutomaticTransitionFromOpenToHalfOpen()
        .waitDurationInOpenState(30.seconds.toJavaDuration())
        .build()

    @Bean
    fun circuitBreakerCached(config: CircuitBreakerConfig): Map<String, CircuitBreaker> {
        val circuits = Origin.values().map {
            CircuitBreaker.of(it.name, config)
        } + CircuitBreaker.of("Default", config)
        return circuits.associateBy { it.name }
    }
}
