package com.picpay.quickstart.events.handlers

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.EventHandler
import com.picpay.quickstart.application.example.ExampleService
import com.picpay.quickstart.application.example.event.ExampleInput
import org.springframework.stereotype.Component

@Component
class ExampleEventHandlerV1(
    private val exampleService: ExampleService
) : EventHandler {

    override val eventName = "example:status:check"
    override val eventVersion = 1

    override suspend fun handle(event: RequestEvent): ResponseEvent {
        val request = event.payloadAs<ExampleInput>()

        return EventBuilder.responseFor(event) {
            payload = exampleService.statusCheck(request)
        }
    }
}
