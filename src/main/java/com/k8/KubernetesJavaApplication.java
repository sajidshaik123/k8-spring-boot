package com.k8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KubernetesJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesJavaApplication.class, args);
		System.out.println(KubernetesJavaApplication.class.getSimpleName()+" started successfully!!!!!!!");

	}


}