package com.picpay.quickstart.health

import com.picpay.quickstart.health.gateways.HealthResponder
import org.springframework.stereotype.Component

@Component
class HealthInteractor(private val healthResponders: List<HealthResponder>) {

    fun retrieveHealthStatuses(): List<ComponentHealth> =
        healthResponders.map {
            ComponentHealth(name = it.name, status = it.isHealth())
        }
}

data class ComponentHealth(val name: String, val status: Boolean)
