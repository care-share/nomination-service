FROM java:8
COPY ./target/nominationService.jar /usr/src/app.jar
WORKDIR /usr/src
CMD ["java", "-jar", "app.jar"]
