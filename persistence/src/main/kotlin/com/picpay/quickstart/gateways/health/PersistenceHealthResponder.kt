package com.picpay.quickstart.gateways.health

import com.picpay.quickstart.health.gateways.HealthResponder
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class PersistenceHealthResponder(private val dataSource: DataSource) : HealthResponder {

    override val name = "DatabaseName"

    override fun isHealth(): Boolean {
        return try {
            dataSource.connection.use {
                it.prepareCall("SELECT 1").execute()
            }
            true
        } catch (ignored: Throwable) {
            false
        }
    }
}
