package com.picpay.quickstart.application.example

import com.picpay.quickstart.application.example.event.ExampleInput
import com.picpay.quickstart.application.example.event.ExampleOutput
import com.picpay.quickstart.application.example.model.ExampleEntity
import com.picpay.quickstart.application.example.repository.ExampleRepository
import com.picpay.quickstart.config.propertiesconfig.ConfigService
import datadog.trace.api.Trace
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Suppress("MagicNumber")
class ExampleService(
    configService: ConfigService,
    private val exampleRepository: ExampleRepository
) {
    private val configVariable = configService.getInt("application.properties.configVariable", 123)

    @Trace(operationName = "ExampleService:statusCheck")
    fun statusCheck(example: ExampleInput): ExampleOutput {
        // your business layer here
        exampleRepository.save(
            ExampleEntity(
                null,
                example.input1,
                LocalDateTime.now()
            )
        )

        return ExampleOutput(
            example.input2,
            (example.input1 + configVariable).toLong()
        )
    }
}
