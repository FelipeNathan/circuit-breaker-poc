package com.picpay.quickstart.repository

import com.picpay.quickstart.application.example.model.ExampleEntity
import com.picpay.quickstart.application.example.repository.ExampleRepository
import com.picpay.quickstart.entity.ExampleTable
import org.springframework.stereotype.Repository

@Repository
class ExampleTableRepositoryImpl(private val repository: IExampleTableRepository) : ExampleRepository {

    override fun save(example: ExampleEntity) {
        val exampleTable = ExampleTable().apply {
            columnNumber1 = example.columnNumber1
            columnNumber2 = "Example"
            columnNumber3 = example.columnNumber3
        }

        repository.save(exampleTable)
    }
}
