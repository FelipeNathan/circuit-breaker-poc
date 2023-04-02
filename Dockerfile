# Builder
FROM gradle:7.5.1-jdk17 AS builder

COPY ./ /home/gradle/project

WORKDIR /home/gradle/project

RUN gradle -Dorg.gradle.daemon=false clean assemble --stacktrace --info

RUN mkdir -p /home/gradle/project/build/distributions/app/

RUN wget -O /home/gradle/dd-java-agent.jar https://repo1.maven.org/maven2/com/datadoghq/dd-java-agent/0.115.0/dd-java-agent-0.115.0.jar

RUN unzip /home/gradle/project/application/build/distributions/*.zip -d /home/gradle/project/build/distributions/app/

# Application
FROM openjdk:17-slim

COPY --from=builder /home/gradle/project/build/distributions/app/ /opt/app/

COPY --from=builder /home/gradle/dd-java-agent.jar /opt/datadog/dd-java-agent.jar

WORKDIR /opt/app

RUN rm -rf /var/cache/*

EXPOSE 8080

CMD ["/opt/app/application-1.0/bin/application"]