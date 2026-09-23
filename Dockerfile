FROM tomcat:10.1-jdk17

COPY target/SimpleChatApp.war /usr/local/tomcat/webapps/SimpleChatApp.war

EXPOSE 8080

CMD ["catalina.sh", "run"]