package com.picpay.quickstart.config.propertiesconfig

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@PropertySource("classpath:/application.properties")
class BaseConfigService(val env: Environment) {

    private val logger = LoggerFactory.getLogger(BaseConfigService::class.java)!!

    fun getString(property: String): String? {
        val value = env.getProperty(toEnvConvention(property))
        if (value != null) {
            logger.info("Config '$property' overwritten with '${toEnvConvention(property)}'.")
            return value
        }
        return env.getProperty(property)
    }

    private fun toEnvConvention(property: String) = property.uppercase().replace(".", "_")
}
