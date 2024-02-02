package com.nasa.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NasaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NasaApiApplication.class, args);
		System.out.println("This is nasa open api.");
	}

}
