package com.picpay.quickstart.application.example.repository

import com.picpay.quickstart.application.example.model.ExampleEntity

interface ExampleRepository {
    fun save(example: ExampleEntity)
}
