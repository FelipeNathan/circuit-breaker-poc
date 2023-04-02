object Dependency {

    object Versions {
        // Kotlin
        const val kotlin = "1.8.0"

        // AWS
        const val awsKms = "1.12.380"

        // Events
        const val events = "6.0.5"

        // Logs
        const val logback = "1.4.5"
        const val logstashEncoder = "7.2"
        const val kotlinLogging = "3.0.4"

        // Spring
        const val springCommon = "6.0.3"

        // Persistence
        const val hikari = "5.0.1"
        const val springJPA = "2.7.6"
        const val hibernateCore = "5.6.14.Final"
        const val mysqlConnector = "8.0.31"

        // Web
        const val ktor = "2.2.2"
        const val protobuf = "3.21.12"

        // Tests
        const val mockk = "1.13.3"
        const val kotest = "5.5.4"
        const val kotestExtensionsSpring = "1.1.2"
        const val junitJupiter = "5.9.1"
        const val detekt = "1.22.0"
        const val h2Database = "2.1.214"

        // Coverage
        const val jacoco = "0.8.8"
    }

    // AWS
    const val awsKms = "com.amazonaws:aws-java-sdk-kms:${Versions.awsKms}"

    // Events
    const val tracing = "br.com.guiabolso:events-tracing:${Versions.events}"
    const val eventsCore = "br.com.guiabolso:events-core:${Versions.events}"
    const val eventsServer = "br.com.guiabolso:events-server:${Versions.events}"
    const val eventsClient = "br.com.guiabolso:events-client:${Versions.events}"
    const val eventsTest = "br.com.guiabolso:events-test:${Versions.events}"

    // Logs
    const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
    const val logbackLogstashEncoder = "net.logstash.logback:logstash-logback-encoder:${Versions.logstashEncoder}"
    const val kotlinLogging = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"

    // Spring
    const val springContext = "org.springframework:spring-context:${Versions.springCommon}"

    // Persistence
    const val hikari = "com.zaxxer:HikariCP:${Versions.hikari}"
    const val springJPA = "org.springframework.data:spring-data-jpa:${Versions.springJPA}"
    const val hibernateCore = "org.hibernate:hibernate-core:${Versions.hibernateCore}"
    const val mysqlConnector = "mysql:mysql-connector-java:${Versions.mysqlConnector}"

    // Web
    const val ktorCore = "io.ktor:ktor-server-core:${Versions.ktor}"
    const val ktorNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"
    const val ktorGson = "io.ktor:ktor-serialization-gson:${Versions.ktor}"
    const val ktorPluginContentNegotiation = "io.ktor:ktor-server-content-negotiation:${Versions.ktor}"
    const val ktorPluginStatusPage = "io.ktor:ktor-server-status-pages:${Versions.ktor}"
    const val protobuf = "com.google.protobuf:protobuf-java:${Versions.protobuf}"

    // Test
    const val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiter}"
    const val kotestCore = "io.kotest:kotest-assertions-core-jvm:${Versions.kotest}"
    const val kotestSpring = "io.kotest.extensions:kotest-extensions-spring:${Versions.kotestExtensionsSpring}"
    const val kotestRunner = "io.kotest:kotest-runner-junit5-jvm:${Versions.kotest}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val ktorTests = "io.ktor:ktor-server-tests:${Versions.ktor}"
    const val detekt = "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}"
    const val springTest = "org.springframework:spring-test:${Versions.springCommon}"
    const val h2Database = "com.h2database:h2:${Versions.h2Database}"
}
