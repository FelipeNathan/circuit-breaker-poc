dependencies {
    implementation(project(":usecases"))

    api(Dependency.hikari)
    api(Dependency.springJPA)
    api(Dependency.hibernateCore)
    api(Dependency.mysqlConnector)

    implementation(Dependency.h2Database)
}