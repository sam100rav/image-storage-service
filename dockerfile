FROM openjdk:8
ADD target/test.jar test.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "test.jar"]