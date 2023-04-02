package com.picpay.quickstart.events.handlers

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.test.shouldBeSuccess
import br.com.guiabolso.events.test.shouldHavePayload
import com.picpay.quickstart.application.example.ExampleService
import com.picpay.quickstart.application.example.event.ExampleOutput
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ExampleEventHandlerV1Test : ShouldSpec({

    val exampleService = mockk<ExampleService>()
    val handler = ExampleEventHandlerV1(exampleService)

    should("Have name example:status:check") {
        handler.eventName shouldBe "example:status:check"
    }

    should("Have version 1") {
        handler.eventVersion shouldBe 1
    }

    should("Handle request event with all payload fields") {
        val event = EventBuilder.event {
            name = "example:status:check"
            version = 1
            id = "event-id"
            flowId = "flow-id"
            payload = mapOf("input1" to 1, "input2" to "2")
            identity = mapOf("userId" to 1)
        }
        every { exampleService.statusCheck(any()) } returns ExampleOutput(outputNumber1 = "1", outputNumber2 = 1L)

        val result = handler.handle(event)

        assertSoftly(result) {
            it.shouldBeSuccess()
            it shouldHavePayload mapOf("outputNumber1" to "1", "outputNumber2" to 1L)
        }
        verify(exactly = 1) { exampleService.statusCheck(any()) }
    }
})
