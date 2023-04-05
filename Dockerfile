# Application
FROM openjdk:17-slim

WORKDIR /opt/app/

COPY ./application-1.0/ /opt/app
CMD ["/opt/app/bin/application"]
