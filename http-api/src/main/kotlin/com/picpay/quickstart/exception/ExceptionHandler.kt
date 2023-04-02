package com.picpay.quickstart.exception

import com.picpay.quickstart.misc.logging.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

object ExceptionHandler {

    private val logger by lazy { logger() }

    fun Application.errorHandler() {
        install(StatusPages) {
            exception<IllegalArgumentException> { call, exception ->
                logger.warn(exception.message, exception)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to exception.message))
            }

            exception<Exception> { call, exception ->
                logger.error(exception.message, exception)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to exception.message))
            }
        }
    }
}
