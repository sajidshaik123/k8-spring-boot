FROM openjdk:17
EXPOSE 8080
ADD target/kubernetes-java-client-k8-java-2022.jar /kubernetes-java-client.jar
ENTRYPOINT ["java", "-jar", "/kubernetes-java-client.jar"]