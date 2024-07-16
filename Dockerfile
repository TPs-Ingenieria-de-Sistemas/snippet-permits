FROM gradle:8.7.0-jdk17-jammy AS build
COPY  . /app
WORKDIR /app
RUN sed -i -e 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

RUN mkdir /app
COPY --from=build /app/build/libs/snippet-permits-*.jar /app/snippet-permits.jar
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar","-jar", "-Dspring.profiles.active=production","/app/snippet-permits.jar"]

