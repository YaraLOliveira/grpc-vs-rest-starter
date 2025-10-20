package com.starter;

import com.starter.grpc.GrpcServer;

/**
 * gRPC Application - HTTP/2 + Protocol Buffers
 * Runs on port 9090
 */
public class GrpcApp {

    public static void main(String[] args) throws Exception {
        System.out.println("\n🟢 Starting gRPC Service");
        System.out.println("⚡ Protocol: HTTP/2");
        System.out.println("📦 Serialization: Protocol Buffers (binary)");
        System.out.println("🌐 Address: localhost:9090");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}