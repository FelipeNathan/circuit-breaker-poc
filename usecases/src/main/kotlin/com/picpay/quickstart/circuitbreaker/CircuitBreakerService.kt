package com.picpay.quickstart.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class CircuitBreakerService(
    @Qualifier("circuitBreakerDefault")
    private val circuitBreakerDefault: CircuitBreaker,
    @Qualifier("circuitBreakerCached")
    private val circuitBreakerCache: CircuitBreaker
) {

    private fun CircuitBreaker.execute(block: () -> OutputCircuitBreaker?): OutputCircuitBreaker? {
        return try {
            executeSupplier(block)
        } catch (ex: CallNotPermittedException) {
            OutputCircuitBreaker("Circuito está aberto: ${ex.message}")
        }
    }

    fun execute(throwException: Boolean) = circuitBreakerDefault.execute {
        println("Executed by default circuit")
        if (throwException) {
            throw IgnoredException()
        }

        OutputCircuitBreaker("Success")
    }

    fun executeCache(throwException: Boolean) = circuitBreakerCache.execute {
        println("Executed by cached circuit")
        if (throwException) {
            throw IgnoredException()
        }

        OutputCircuitBreaker("Success")
    }

    data class OutputCircuitBreaker(
        val body: String
    )
}
