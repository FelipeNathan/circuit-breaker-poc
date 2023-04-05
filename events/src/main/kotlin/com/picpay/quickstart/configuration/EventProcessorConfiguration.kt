package com.picpay.quickstart.configuration

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.server.SuspendingEventProcessor
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import com.google.gson.GsonBuilder
import com.picpay.quickstart.circuitbreaker.IgnoredException
import com.picpay.quickstart.misc.tracing.Tracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventProcessorConfiguration {

    @Bean
    fun eventHandlerDiscovery(eventsHandlers: List<EventHandler>) = SimpleEventHandlerRegistry().apply {
        eventsHandlers.forEach(::add)
    }

    @Bean
    fun eventExceptionHandlerRegistry() = exceptionHandler().apply {
        register(IgnoredException::class.java, IgnoredExceptionHandler)
    }

    @Bean
    fun eventProcessor(
        eventHandlerDiscovery: EventHandlerDiscovery,
        exceptionHandlerRegistry: ExceptionHandlerRegistry
    ): SuspendingEventProcessor {
        MapperHolder.mapper = GsonBuilder().serializeNulls().create()
        return SuspendingEventProcessor(eventHandlerDiscovery, exceptionHandlerRegistry, Tracer)
    }
}
