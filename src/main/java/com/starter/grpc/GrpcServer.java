package com.starter.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * gRPC Server - Manages server lifecycle
 */
public class GrpcServer {

    private Server server;
    private static final int PORT = 9090;

    /**
     * Start the gRPC server
     */
    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new UserServiceImpl())
                .build()
                .start();

        System.out.println("✅ gRPC Server started successfully");
        System.out.println("📡 Listening on port: " + PORT);
        System.out.println("🔧 Service: UserService");
        System.out.println();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("\n🛑 Shutting down gRPC server (JVM shutdown)...");
            GrpcServer.this.stop();
            System.err.println("✅ Server shut down successfully");
        }));
    }

    /**
     * Stop the gRPC server
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Block until the server shuts down
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}