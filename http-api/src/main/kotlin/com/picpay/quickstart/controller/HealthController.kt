package com.picpay.quickstart.controller

import com.picpay.quickstart.health.HealthInteractor
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.health(interactor: HealthInteractor) {
    get("/health") {
        val healthStatuses = interactor.retrieveHealthStatuses()

        val httpStatusCode = if (healthStatuses.any { !it.status }) {
            HttpStatusCode.InternalServerError
        } else {
            HttpStatusCode.OK
        }

        call.respond(httpStatusCode, healthStatuses)
    }
}
