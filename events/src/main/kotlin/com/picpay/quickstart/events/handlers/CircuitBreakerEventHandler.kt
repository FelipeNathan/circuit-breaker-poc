package com.picpay.quickstart.events.handlers

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.EventHandler
import com.picpay.quickstart.circuitbreaker.CircuitBreakerService
import org.springframework.stereotype.Service

@Service
class CircuitBreakerEventHandler(
    val circuitBreakerService: CircuitBreakerService
) : EventHandler {
    override val eventName = "circuit:breaker"
    override val eventVersion = 1

    override suspend fun handle(event: RequestEvent): ResponseEvent {
        val input = event.payloadAs<InputCircuitBreaker>()

        val response = if (input.isCached) {
            circuitBreakerService.executeCache(input.throwException)
        } else {
            circuitBreakerService.execute(input.throwException)
        }

        return EventBuilder.responseFor(event) {
            payload = response
        }
    }

    data class InputCircuitBreaker(
        val throwException: Boolean,
        val isCached: Boolean
    )
}
