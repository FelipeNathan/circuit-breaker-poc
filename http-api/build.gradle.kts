dependencies {
    implementation(project(":usecases"))
    implementation(project(":events"))

    api(Dependency.ktorNetty)
    api(Dependency.ktorCore)
    api(Dependency.ktorGson)
    api(Dependency.ktorPluginContentNegotiation)
    api(Dependency.ktorPluginStatusPage)
    api(Dependency.eventsServer)
    // Temporary dependency due the vulnerability CVE-2022-3510
    api(Dependency.protobuf)

    testImplementation(Dependency.ktorTests)
}