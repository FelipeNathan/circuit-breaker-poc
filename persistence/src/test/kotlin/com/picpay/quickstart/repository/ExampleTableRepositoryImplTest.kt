package com.picpay.quickstart.repository

import com.picpay.quickstart.application.example.model.ExampleEntity
import com.picpay.quickstart.entity.ExampleTable
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class ExampleTableRepositoryImplTest : FunSpec() {

    private val repository = mockk<IExampleTableRepository> {
        every { save(any<ExampleTable>()) } returns mockk()
    }
    private val target = ExampleTableRepositoryImpl(repository)

    init {
        test("Save") {
            val et = ExampleEntity(1, 1, LocalDateTime.now())

            target.save(et)

            verify(exactly = 1) { repository.save(any<ExampleTable>()) }
        }
    }
}
