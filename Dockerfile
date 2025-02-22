FROM amd64/amazoncorretto:17

WORKDIR /app

COPY ./build/libs/newquiz-0.0.1-SNAPSHOT.jar /app/newquiz.jar

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=dev", "/app/newquiz.jar"]