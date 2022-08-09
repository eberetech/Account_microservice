#
FROM openjdk:8-slim as build
EXPOSE 8080
#
MAINTAINER adekzs

#
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar 

#
ENTRYPOINT ["java", "-jar","/accounts-0.0.1-SNAPSHOT.jar"]
