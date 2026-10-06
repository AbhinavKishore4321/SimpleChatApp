FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


FROM tomcat:10.1-jdk17

COPY --from=build /app/target/SimpleChatApp.war \
    /usr/local/tomcat/webapps/SimpleChatApp.war

ENV CHAT_DATA_DIR=/data

VOLUME ["/data"]

EXPOSE 8080

CMD ["catalina.sh", "run"]