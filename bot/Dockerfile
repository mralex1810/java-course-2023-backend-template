FROM ubuntu:latest
LABEL authors="chulkov-alex"

ADD target/bot.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
