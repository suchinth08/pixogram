FROM openjdk:11
EXPOSE 8080
ADD backend/target/backend-0.0.1-SNAPSHOT.jar backend-0.0.1-SNAPSHOT.jar
ADD frontend/target/frontend-0.0.1-SNAPSHOT.jar frontend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/backend-0.0.1-SNAPSHOT.jar"]
RUN java -jar frontend-0.0.1-SNAPSHOT.jar

