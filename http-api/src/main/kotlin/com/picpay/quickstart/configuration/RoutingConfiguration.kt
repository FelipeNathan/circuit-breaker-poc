package com.picpay.quickstart.configuration

import br.com.guiabolso.events.server.SuspendingEventProcessor
import com.picpay.quickstart.controller.events
import com.picpay.quickstart.controller.health
import com.picpay.quickstart.health.HealthInteractor
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoutingConfiguration {
    @Bean
    fun configureRoutes(
        application: Application,
        eventProcessor: SuspendingEventProcessor,
        healthInteractor: HealthInteractor
    ) = with(application) {
        routing {
            health(healthInteractor)
            events(eventProcessor)
        }
    }
}
