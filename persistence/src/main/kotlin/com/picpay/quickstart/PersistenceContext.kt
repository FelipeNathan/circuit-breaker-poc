package com.picpay.quickstart

import com.picpay.quickstart.config.propertiesconfig.ConfigService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@Suppress("MagicNumber")
@EnableJpaRepositories(basePackages = ["com.picpay.quickstart"])
class PersistenceContext {

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }

    @Bean
    fun entityManagerFactory(config: ConfigService, dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.setPackagesToScan("com.picpay.quickstart")

        val jpaProperties = Properties().apply {
            put("hibernate.dialect", config.getRequiredString("dbName.hibernate.dialect"))
            put("hibernate.hbm2ddl.auto", config.getString("dbName.datasource.ddl", "validate"))
            put(
                "hibernate.hbm2ddl.import_files_sql_extractor",
                "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor"
            )
            put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy")
            put("hibernate.show_sql", "false")
            put("hibernate.format_sql", "false")

            put("hibernate.jdbc.batch_size", "30")
            put("hibernate.order_inserts", "true")
            put("hibernate.order_updates", "true")
        }

        entityManagerFactoryBean.setJpaProperties(jpaProperties)

        return entityManagerFactoryBean
    }

    @Bean
    fun getDataSource(config: ConfigService): DataSource {
        val hikariConfig = HikariConfig()

        hikariConfig.driverClassName = config.getRequiredString("dbName.datasource.driverClassName")
        hikariConfig.jdbcUrl = config.getRequiredString("dbName.datasource.url")
        hikariConfig.username = config.getRequiredString("dbName.datasource.username")
        hikariConfig.password = config.getString("dbName.datasource.password", "")

        hikariConfig.minimumIdle = config.getInt("dbName.datasource.minimumIdle", 1)
        hikariConfig.maximumPoolSize = config.getInt("dbName.datasource.maximumPoolSize", 15)

        hikariConfig.connectionTestQuery = "SELECT 1"

        return HikariDataSource(hikariConfig)
    }
}
