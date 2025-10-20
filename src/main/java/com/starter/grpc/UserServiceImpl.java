package com.starter.grpc;

import com.starter.grpc.proto.*;
import com.starter.model.User;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * gRPC Service Implementation
 * Demonstrates all 4 types of RPC
 */
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    // In-memory database
    private final Map<Long, User> database = new ConcurrentHashMap<>();
    private long nextId = 1;

    public UserServiceImpl() {
        // Seed with sample data
        for (int i = 1; i <= 10; i++) {
            User user = new User(
                    (long) i,
                    "User " + i,
                    "user" + i + "@example.com",
                    20 + i
            );
            database.put((long) i, user);
        }
        nextId = 11;

        System.out.println("✅ gRPC Service initialized with " + database.size() + " users");
    }

    /**
     * 1. UNARY RPC - Simple Request/Response
     * Equivalent to REST GET
     */
    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        System.out.println("🟢 gRPC UNARY - GetUser: " + request.getId());

        User user = database.get(request.getId());

        if (user == null) {
            responseObserver.onError(
                    new RuntimeException("User not found: " + request.getId())
            );
            return;
        }

        UserResponse response = UserResponse.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setAge(user.getAge())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 2. SERVER STREAMING - Server sends multiple responses
     * IMPOSSIBLE in traditional REST!
     */
    @Override
    public void listUsers(ListRequest request, StreamObserver<UserResponse> responseObserver) {
        System.out.println("🟢 gRPC SERVER STREAMING - ListUsers: limit=" + request.getLimit());

        int limit = request.getLimit() > 0 ? request.getLimit() : 10;
        int count = 0;

        for (User user : database.values()) {
            if (count >= limit) break;

            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setAge(user.getAge())
                    .build();

            // Send each user individually (streaming)
            responseObserver.onNext(response);
            System.out.println("   → Streamed user: " + user.getName());

            count++;

            // Simulate processing delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        responseObserver.onCompleted();
        System.out.println("   ✓ Stream completed: " + count + " users sent");
    }

    /**
     * 3. CLIENT STREAMING - Client sends multiple requests
     * Advantage: Process incrementally, no need to load everything in memory
     */
    @Override
    public StreamObserver<CreateUserRequest> createUsers(
            StreamObserver<CreateSummary> responseObserver) {

        System.out.println("🟢 gRPC CLIENT STREAMING - CreateUsers starting...");

        return new StreamObserver<CreateUserRequest>() {
            private int count = 0;

            @Override
            public void onNext(CreateUserRequest request) {
                // Process each user as received
                User user = new User(
                        nextId++,
                        request.getName(),
                        request.getEmail(),
                        request.getAge()
                );
                database.put(user.getId(), user);
                count++;
                System.out.println("   ← Received user " + count + ": " + user.getName());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("   ✗ Error in stream: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                // Send summary after receiving all
                CreateSummary summary = CreateSummary.newBuilder()
                        .setTotalCreated(count)
                        .setMessage("Successfully created " + count + " users")
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();

                System.out.println("   ✓ Client stream completed: " + count + " users created");
            }
        };
    }

    /**
     * 4. BIDIRECTIONAL STREAMING - Both stream simultaneously
     * For chat, real-time, gaming, etc.
     */
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        System.out.println("🟢 gRPC BIDIRECTIONAL STREAMING - Chat starting...");

        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                System.out.println("   ← Chat received: " + message.getMessage());

                // Respond immediately
                ChatMessage response = ChatMessage.newBuilder()
                        .setMessage("Echo: " + message.getMessage())
                        .setTimestamp(System.currentTimeMillis())
                        .build();

                responseObserver.onNext(response);
                System.out.println("   → Chat sent: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("   ✗ Chat error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                System.out.println("   ✓ Chat completed");
            }
        };
    }
}