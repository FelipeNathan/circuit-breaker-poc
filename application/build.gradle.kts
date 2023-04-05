plugins {
    application
}

application {
    mainClass.set("com.picpay.quickstart.BootKt")

    applicationDefaultJvmArgs = listOf(
        "-server",
        "-XX:+UseNUMA",
        "-XX:+UseParallelGC",
        "-Duser.timezone=UTC"
    )
}

dependencies {
    implementation(project(":usecases"))
//    implementation(project(":persistence"))
    implementation(project(":http-api"))
}