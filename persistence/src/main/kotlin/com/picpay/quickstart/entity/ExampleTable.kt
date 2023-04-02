package com.picpay.quickstart.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ExampleTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null

    @Column(nullable = false)
    var columnNumber1: Int? = null

    @Column(nullable = false)
    lateinit var columnNumber2: String

    @Column(nullable = false)
    lateinit var columnNumber3: LocalDateTime

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (columnNumber1 ?: 0)
        result = 31 * result + columnNumber2.hashCode()
        result = 31 * result + columnNumber3.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val ob = other as ExampleTable
        return ob.id != null && this.id != null && ob.id == this.id
    }
}
