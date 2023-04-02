package com.picpay.quickstart

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringAutowireConstructorExtension

class ProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> = listOf(SpringAutowireConstructorExtension)
}
