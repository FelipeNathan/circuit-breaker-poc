package com.picpay.quickstart

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.picpay.quickstart"])
class Boot

fun main() {
    AnnotationConfigApplicationContext(Boot::class.java)
}
