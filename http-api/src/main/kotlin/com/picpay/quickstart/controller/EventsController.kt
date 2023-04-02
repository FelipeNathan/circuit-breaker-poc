package com.picpay.quickstart.controller

import br.com.guiabolso.events.server.SuspendingEventProcessor
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.events(eventProcessor: SuspendingEventProcessor) {
    post("/events") {
        call.respondText(
            text = eventProcessor.processEvent(call.receiveText()),
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.OK
        )
    }
}
