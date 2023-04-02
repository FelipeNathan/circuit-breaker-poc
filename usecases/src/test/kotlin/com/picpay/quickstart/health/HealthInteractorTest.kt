package com.picpay.quickstart.health

import com.picpay.quickstart.health.gateways.HealthResponder
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class HealthInteractorTest : BehaviorSpec({
    val responders = listOf<HealthResponder>(mockk(), mockk())
    val interactor = HealthInteractor(responders)

    Given("First responder is healthy but second responder isn't") {
        responders.forEachIndexed { index, healthResponder ->
            every { healthResponder.name } returns "Responder #$index"
            every { healthResponder.isHealth() } returns (index == 0)
        }

        When("The health status is retrieved") {
            val result = interactor.retrieveHealthStatuses()

            Then("The first result should be healthy") {
                result[0].name shouldBe "Responder #0"
                result[0].status shouldBe true
            }

            Then("The second result should be unhealthy") {
                result[1].name shouldBe "Responder #1"
                result[1].status shouldBe false
            }
        }
    }
})
