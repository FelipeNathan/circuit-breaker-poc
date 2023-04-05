import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Dependency.Versions.kotlin
    kotlin("plugin.allopen") version Dependency.Versions.kotlin
    kotlin("plugin.noarg") version Dependency.Versions.kotlin
//    id("io.gitlab.arturbosch.detekt") version Dependency.Versions.detekt
    jacoco
}

repositories {
    mavenCentral()
}

allprojects {
    version = "1.0"
    group = "com.picpay"
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-jpa")
    apply(plugin = "jacoco")
//    apply(plugin = "io.gitlab.arturbosch.detekt")

//    detekt {
//        autoCorrect = true
//    }

    repositories {
        mavenCentral()
    }

    jacoco {
        toolVersion = Dependency.Versions.jacoco
        reportsDirectory.set(file("$buildDir/reports/jacoco"))
    }

    allOpen {
        annotation("javax.persistence.Entity")
        annotation("javax.persistence.MappedSuperclass")
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(true)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "${JavaVersion.VERSION_17}"
            allWarningsAsErrors = true
        }
    }

    dependencies {
        // Kotlin
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        // Logs
        implementation(Dependency.logback)
        implementation(Dependency.logbackLogstashEncoder)
        implementation(Dependency.kotlinLogging)

        // Spring
        implementation(Dependency.springContext)

        // KMS
        implementation(Dependency.awsKms)

        // Events
        implementation(Dependency.eventsCore)

        // Tracing
        implementation(Dependency.tracing)

        // Test
        testImplementation(Dependency.kotestRunner)
        testImplementation(Dependency.kotestSpring)
        testImplementation(Dependency.kotestCore)
        testImplementation(Dependency.junitJupiterEngine)
        testImplementation(Dependency.mockk)
        testImplementation(Dependency.springTest)
        testImplementation(Dependency.eventsTest)

//        detektPlugins(Dependency.detekt)
    }
}

jacoco {
    toolVersion = Dependency.Versions.jacoco
}

tasks.register<JacocoReport>("codeCoverageReport") {
    jacoco { toolVersion = Dependency.Versions.jacoco }
    subprojects.forEach { subproject ->
        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks
                .matching { it.extensions.findByType<JacocoTaskExtension>() != null }
                .configureEach {
                    sourceSets(subproject.sourceSets.main.get())
                    executionData(
                        files(this).filter { it.isFile && it.exists() }
                    )
                }
            subproject.tasks
                .matching { it.extensions.findByType<JacocoTaskExtension>() != null }
                .forEach { rootProject.tasks["codeCoverageReport"].dependsOn(it) }
            subproject.tasks
                .withType<Test>()
                .forEach { rootProject.tasks["codeCoverageReport"].dependsOn(it) }
        }
    }
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}