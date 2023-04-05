package com.picpay.quickstart.configuration

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.handler.EventExceptionHandler
import br.com.guiabolso.tracing.Tracer
import com.picpay.quickstart.circuitbreaker.IgnoredException

object IgnoredExceptionHandler : EventExceptionHandler<IgnoredException> {
    override suspend fun handleException(exception: IgnoredException, event: RequestEvent, tracer: Tracer): ResponseEvent {
        return EventBuilder.errorFor(event, EventErrorType.Generic, EventMessage(
            exception.code,
            exception.parameters
        ))
    }
}