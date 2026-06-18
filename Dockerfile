FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn package

CMD ["mvn", "exec:java"]