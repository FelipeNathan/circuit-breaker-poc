package com.picpay.quickstart.gateways.health

import com.picpay.quickstart.PersistenceTestSetup
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ContextConfiguration
import javax.sql.DataSource

@ContextConfiguration(classes = [PersistenceTestSetup::class])
class PersistenceHealthResponderTest(
    private val dataSource: DataSource,
    private val responder: PersistenceHealthResponder
) : FunSpec() {

    init {
        test("Should return true when database is available") {
            responder.isHealth() shouldBe true
        }

        test("Should return false when database is unavailable") {
            dataSource.unwrap(HikariDataSource::class.java).close()

            responder.isHealth() shouldBe false
        }
    }
}
