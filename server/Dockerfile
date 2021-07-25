FROM maven:3.8.1-openjdk-16-slim AS package
COPY src /home/server/src
COPY pom.xml /home/server/pom.xml
RUN mvn -f /home/server/pom.xml clean package spring-boot:repackage

FROM openjdk:16-slim
COPY --from=package /home/server/target/server-1.0.0.jar /usr/local/lib/server-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/server-1.0.0.jar"]
