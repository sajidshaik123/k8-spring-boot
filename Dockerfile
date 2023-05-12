FROM openjdk:20
EXPOSE 8081
ADD target/kubernetes-java-client-k8-java-2022.jar /kubernetes-java-client.jar
ENTRYPOINT ["java", "-jar", "/kubernetes-java-client.jar"]