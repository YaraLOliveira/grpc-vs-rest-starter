package com.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST Application - HTTP/1.1 + JSON
 * Runs on port 8080
 */
@SpringBootApplication
public class RestApp {

    public static void main(String[] args) {
        System.out.println("\n🔵 Starting REST Service");
        System.out.println("📄 Protocol: HTTP/1.1");
        System.out.println("📦 Serialization: JSON (Jackson)");
        System.out.println("🌐 URL: http://localhost:8080");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        SpringApplication.run(RestApp.class, args);
    }
}