package com.picpay.quickstart.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.stereotype.Service

@Service
class CircuitBreakerService(
    private val circuits: Map<String, CircuitBreaker>
) {

    private fun CircuitBreaker.execute(block: () -> OutputCircuitBreaker?): OutputCircuitBreaker? {
        return try {
            executeSupplier(block)
        } catch (ex: CallNotPermittedException) {
            OutputCircuitBreaker("Circuito está aberto: ${ex.message}")
        }
    }

    fun execute(origin: String?, throwException: Boolean): OutputCircuitBreaker? {
        val circuit = circuits[origin] ?: circuits["Default"]!!

        return circuit.execute {
            println("Executed by ${circuit.name} circuit")
            if (throwException) {
                throw IgnoredException()
            }

            OutputCircuitBreaker("Success")
        }
    }

    data class OutputCircuitBreaker(
        val body: String
    )
}
