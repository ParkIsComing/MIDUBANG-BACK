FROM openjdk:11

WORKDIR /src

COPY build/libs/midubang-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]