FROM ubuntu:latest
LABEL authors="chulkov-alex"

ADD target/scrapper.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
