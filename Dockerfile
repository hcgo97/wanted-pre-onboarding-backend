FROM khipu/openjdk17-alpine

# work directory 설정
WORKDIR /app

# spring profile 설정
ARG SPRING_PROFILE

# local -> docker
ARG BUILD_JAR=build/libs/*.jar
COPY ${BUILD_JAR} ./wanted-internship.jar

# docker run
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "./wanted-internship.jar"]
