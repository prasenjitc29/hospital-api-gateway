package com.hos.hosgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class HosGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HosGatewayApplication.class, args);
	}

}
