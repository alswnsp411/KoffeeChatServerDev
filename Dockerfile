# BUILD (이미지를 만드는 과정)
FROM gradle:7.6.1-jdk17-alpine as builder

WORKDIR /app
COPY . .

RUN ["gradle", "-x", "test" , "build"]

# Base Image
FROM openjdk:17-alpine

# 운영 환경 컨테이너dockr
WORKDIR /app

# 프로젝트를 빌드한 파일을 변수에 저장
ARG JAR_FILE=./build/libs/koffeeChat-0.0.1-SNAPSHOT.jar

# 컨테이너 WORKDIR 위치에 jar 파일 복사
COPY ${JAR_FILE} coffechat-server.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "coffechat-server.jar"]