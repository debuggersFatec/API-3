package com.api_3.api_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class Api3Application {

	public static void main(String[] args) {
		// Load .env if present, but don't fail if it's missing; prefer real environment variables
		Dotenv dotenv = Dotenv.configure()
			.ignoreIfMalformed()
			.ignoreIfMissing()
			.load();

		String mongoUrl = System.getenv("MONGO_URL");
		if (mongoUrl == null) mongoUrl = dotenv.get("MONGO_URL");
		// Fallback to parent directory (e.g., back-end/.env) when running from back-end/api-3
		if ((mongoUrl == null || mongoUrl.isBlank())) {
			Dotenv parentEnv = Dotenv.configure()
				.directory("../")
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();
			mongoUrl = parentEnv.get("MONGO_URL");
		}
		if (mongoUrl != null && !mongoUrl.isBlank()) {
			System.setProperty("MONGO_URL", mongoUrl);
		}

		String jwtSecret = System.getenv("JWT_SECRET");
		if (jwtSecret == null) jwtSecret = dotenv.get("JWT_SECRET");
		if ((jwtSecret == null || jwtSecret.isBlank())) {
			Dotenv parentEnv = Dotenv.configure()
				.directory("../")
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();
			jwtSecret = parentEnv.get("JWT_SECRET");
		}
		if (jwtSecret != null && !jwtSecret.isBlank()) {
			System.setProperty("JWT_SECRET", jwtSecret);
		}

		SpringApplication.run(Api3Application.class, args);
	}

}