package com.picpay.quickstart.configuration

import io.ktor.serialization.gson.gson
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.loadCommonConfiguration
import io.ktor.server.engine.stop
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Configuration
class KtorConfiguration {

    @Bean
    fun ktorEngine(): ApplicationEngine {
        val applicationEnvironment = commandLineEnvironment(emptyArray())
        val engine = NettyApplicationEngine(applicationEnvironment) { loadConfiguration(applicationEnvironment.config) }
        engine.addShutdownHook {
            engine.stop(gracePeriod = GRACE_PERIOD, timeout = TIMEOUT, timeUnit = TIME_UNIT)
        }
        return engine.apply {
            thread(start = true) {
                start(wait = true)
            }
        }
    }

    @Bean
    fun application(engine: ApplicationEngine) = engine.application.apply {
        install(ContentNegotiation) { gson {} }
    }

    private fun NettyApplicationEngine.Configuration.loadConfiguration(config: ApplicationConfig) {
        val deploymentConfig = config.config("ktor.deployment")
        loadCommonConfiguration(deploymentConfig)

        deploymentConfig.booleanProperty("shareWorkGroup")?.let { shareWorkGroup = it }
        deploymentConfig.intProperty("requestQueueLimit")?.let { requestQueueLimit = it }
        deploymentConfig.intProperty("responseWriteTimeoutSeconds")?.let { responseWriteTimeoutSeconds = it }
        deploymentConfig.intProperty("callGroupSize")?.let { callGroupSize = it }
        deploymentConfig.intProperty("workerGroupSize")?.let { workerGroupSize = it }
        deploymentConfig.intProperty("runningLimit")?.let { runningLimit = it }
    }

    private fun ApplicationConfig.intProperty(name: String) = propertyOrNull(name)?.getString()?.toInt()

    private fun ApplicationConfig.booleanProperty(name: String) = propertyOrNull(name)?.getString()?.toBoolean()

    private companion object {
        const val GRACE_PERIOD = 3L
        const val TIMEOUT = 5L
        val TIME_UNIT = TimeUnit.SECONDS
    }
}
