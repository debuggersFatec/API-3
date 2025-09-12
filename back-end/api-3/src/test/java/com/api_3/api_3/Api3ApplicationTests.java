package com.api_3.api_3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class Api3ApplicationTests {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("MONGO_URL", dotenv.get("MONGO_URL"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    }

    @Test
    void contextLoads() {
    }

}