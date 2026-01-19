package com.ajex.invoice.staging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InvoiceStagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceStagingApplication.class, args);
		System.out.println("InvoiceStagingApplication Started...");
	}

}
