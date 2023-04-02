package com.picpay.quickstart.health.gateways

interface HealthResponder {

    val name: String

    fun isHealth(): Boolean
}
