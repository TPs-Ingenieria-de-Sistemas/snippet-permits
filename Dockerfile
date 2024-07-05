FROM gradle:8.7.0-jdk17-jammy AS build
COPY  . /app
WORKDIR /app
RUN sed -i -e 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
RUN mkdir /app
COPY --from=build /app/build/libs/snippet-permits-0.0.1-SNAPSHOT.jar /app/snippet-permits.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","/app/snippet-permits.jar"]
