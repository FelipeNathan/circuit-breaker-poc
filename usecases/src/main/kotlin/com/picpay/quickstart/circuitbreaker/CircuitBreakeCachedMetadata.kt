package com.picpay.quickstart.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import java.util.LinkedList
import java.util.Queue

data class CircuitBreakeCachedMetadata(
    var state: CircuitBreaker.State = CircuitBreaker.State.CLOSED,
    val request: Queue<Boolean> = LinkedList(),
    val maxSize: Int = 100,
    val cacheKeyName: String,
) {
    fun update(success: Boolean) {
        request.add(success)
        if (request.size > maxSize)
            request.remove()
    }

    fun calculateErrorRate(): Double {
        val errors = request.filter { !it }.size.toDouble()
        val success = request.filter { it }.size.toDouble()
        return (errors / (errors + success)) * 100
    }
}