package com.picpay.quickstart

import com.picpay.quickstart.config.propertiesconfig.ConfigService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan("com.picpay.quickstart.*")
@PropertySource("classpath:/database-test.properties")
@EnableJpaRepositories(basePackages = ["com.picpay.quickstart.*"])
@Suppress("UnusedPrivateMember")
class PersistenceTestSetup(private val config: ConfigService) {

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            this.entityManagerFactory = entityManagerFactory
        }
    }

    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val jpaProperties = Properties().apply {
            this["hibernate.dialect"] = "org.hibernate.dialect.H2Dialect"
            this["hibernate.ejb.naming_strategy"] = "org.hibernate.cfg.ImprovedNamingStrategy"
            this["hibernate.show_sql"] = false
            this["hibernate.format_sql"] = true
            this["hibernate.hbm2ddl.auto"] = "create"
        }

        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setPackagesToScan("com.picpay.quickstart.*")
            setJpaProperties(jpaProperties)
        }
    }

    @Bean
    fun dataSource(env: Environment): DataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.getRequiredString("database.test.url")
            username = config.getRequiredString("database.test.user")
            password = config.getRequiredString("database.test.password")
            maximumPoolSize = config.getRequiredInt("database.test.max.pool")
            minimumIdle = config.getRequiredInt("database.test.minimum.idle")
        }
    )
}
