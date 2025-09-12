package com.api_3.api_3;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Api3Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("MONGO_URL", dotenv.get("MONGO_URL"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

		SpringApplication.run(Api3Application.class, args);
	}

}
