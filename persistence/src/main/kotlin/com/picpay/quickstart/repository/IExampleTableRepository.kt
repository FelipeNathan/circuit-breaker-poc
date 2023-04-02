package com.picpay.quickstart.repository

import com.picpay.quickstart.entity.ExampleTable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IExampleTableRepository : CrudRepository<ExampleTable, Long> {
    fun findTopByOrderByIdDesc(): ExampleTable?
}
