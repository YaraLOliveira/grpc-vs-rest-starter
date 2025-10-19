# gRPC vs REST - Minimal Starter 🚀

> **Educational project** to understand the differences between gRPC and REST in practice, with simple and straightforward code.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 🎯 What Does This Project Do?

Implements **the same user service** in two different ways:
- 🔵 **REST**: HTTP/1.1 + JSON (traditional)
- 🟢 **gRPC**: HTTP/2 + Protocol Buffers (modern)

**You can directly compare**: performance, payload size, ease of use, and more.

---

## 🚀 Quick Start (5 minutes)

### Prerequisites

- Java 17+
- Maven 3.8+

### 1. Clone and Compile

```bash
git clone https://github.com/YaraLOliveira/grpc-vs-rest-starter.git
cd grpc-vs-rest-starter
mvn clean install
```

### 2. Run REST (Terminal 1)

```bash
chmod +x run-rest.sh
./run-rest.sh
```

Or:
```bash
mvn exec:java -Dexec.mainClass="com.starter.RestApp"
```

✅ REST running at `http://localhost:8080`

### 3. Run gRPC (Terminal 2)

```bash
chmod +x run-grpc.sh
./run-grpc.sh
```

Or:
```bash
mvn exec:java -Dexec.mainClass="com.starter.GrpcApp"
```

✅ gRPC running at `localhost:9090`

### 4. Test (Terminal 3)

```bash
# Test REST
curl http://localhost:8080/api/users/1

# Test gRPC (requires grpcurl)
grpcurl -plaintext -d '{"id": 1}' localhost:9090 user.UserService/GetUser

# Or use the test script
./test-both.sh
```

---

## 📚 What's Implemented?

### REST Service (Spring Boot)

**Endpoints:**
```bash
GET    /api/users/{id}      # Get user
GET    /api/users?limit=10  # List users
POST   /api/users           # Create user
GET    /api/users/health    # Health check
```

**Example:**
```bash
curl http://localhost:8080/api/users/1
```

**Response (JSON, ~487 bytes):**
```json
{
  "id": 1,
  "name": "User 1",
  "email": "user1@example.com",
  "age": 21
}
```

### gRPC Service

**Methods (4 RPC types):**

1. **Unary RPC** - Simple Request/Response
```bash
grpcurl -plaintext -d '{"id": 1}' localhost:9090 user.UserService/GetUser
```

2. **Server Streaming** - Server sends multiple responses
```bash
grpcurl -plaintext -d '{"limit": 5}' localhost:9090 user.UserService/ListUsers
```

3. **Client Streaming** - Client sends multiple requests
```bash
# Use Java client or language that supports streaming
```

4. **Bidirectional Streaming** - Both stream
```bash
# Real-time chat - use Java client
```

**Response (Protobuf binary, ~68 bytes):**
```protobuf
{
  "id": 1,
  "name": "User 1",
  "email": "user1@example.com",
  "age": 21
}
```

---

## 🔬 Experiments to Try

### 1. Compare Payload Sizes

```bash
# REST
curl -s http://localhost:8080/api/users/1 | wc -c
# Result: ~487 bytes

# gRPC
# ~68 bytes (binary)

# Reduction: 86%!
```

### 2. Measure Latency

```bash
# REST
time curl -s http://localhost:8080/api/users/1 > /dev/null

# gRPC
time grpcurl -plaintext -d '{"id": 1}' localhost:9090 user.UserService/GetUser > /dev/null
```

### 3. Test Streaming (gRPC only)

```bash
# Server Streaming - observe users being sent one by one
grpcurl -plaintext -d '{"limit": 5}' localhost:9090 user.UserService/ListUsers
```

### 4. Modify the Contract

**File:** `src/main/proto/user.proto`

Add a field:
```protobuf
message UserResponse {
  int64 id = 1;
  string name = 2;
  string email = 3;
  int32 age = 4;
  string phone = 5;  // ← NEW FIELD
}
```

Recompile:
```bash
mvn clean compile
```

**See the magic:** Java code is automatically regenerated with the new field! Type safety guaranteed.

---

## 📊 Visual Comparison

| Aspect | REST | gRPC | Winner |
|---------|------|------|----------|
| **Setup** | ⭐⭐⭐⭐⭐ Very easy | ⭐⭐⭐ Needs `.proto` | REST |
| **Payload** | 487 bytes | 68 bytes | gRPC 🏆 |
| **Latency** | ~15-30ms | ~2-8ms | gRPC 🏆 |
| **Streaming** | ❌ No | ✅ 4 types | gRPC 🏆 |
| **Type Safety** | ⚠️ Runtime | ✅ Compile-time | gRPC 🏆 |
| **Debug** | ⭐⭐⭐⭐⭐ Readable JSON | ⭐⭐⭐ Binary | REST |
| **Browser** | ✅ Native | ⚠️ Needs gRPC-Web | REST |

---

## 🛠️ Useful Tools

### grpcurl (gRPC CLI client)

**Install:**
```bash
# macOS
brew install grpcurl

# Linux
wget https://github.com/fullstorydev/grpcurl/releases/download/v1.8.9/grpcurl_1.8.9_linux_x86_64.tar.gz
tar -xvf grpcurl_1.8.9_linux_x86_64.tar.gz
sudo mv grpcurl /usr/local/bin/
```

**Use:**
```bash
# List services
grpcurl -plaintext localhost:9090 list

# List methods
grpcurl -plaintext localhost:9090 list user.UserService

# Call method
grpcurl -plaintext -d '{"id": 1}' localhost:9090 user.UserService/GetUser
```

### Apache Bench (REST benchmark)

```bash
# 1000 requests, 10 concurrent
ab -n 1000 -c 10 http://localhost:8080/api/users/1
```

### ghz (gRPC benchmark)

**Install:**
```bash
go install github.com/bojand/ghz/cmd/ghz@latest
```

**Use:**
```bash
ghz --insecure \
  --proto src/main/proto/user.proto \
  --call user.UserService.GetUser \
  -d '{"id":1}' \
  -n 1000 \
  -c 10 \
  localhost:9090
```

---

## 📂 Project Structure

```
grpc-vs-rest-starter/
├── pom.xml                              # Maven dependencies
├── run-rest.sh                          # REST script
├── run-grpc.sh                          # gRPC script
├── test-both.sh                         # Test scripts
│
└── src/main/
    ├── java/com/starter/
    │   ├── RestApp.java                 # 🔵 REST Application
    │   ├── GrpcApp.java                 # 🟢 gRPC Application
    │   │
    │   ├── model/
    │   │   └── User.java                # Shared model
    │   │
    │   ├── rest/
    │   │   └── UserController.java      # REST Controller
    │   │
    │   └── grpc/
    │       ├── GrpcServer.java          # gRPC Server
    │       └── UserServiceImpl.java     # Implementation
    │
    └── proto/
        └── user.proto                   # ⭐ gRPC Contract
```

---

## 🎓 Demonstrated Concepts

### 1. Strong Contract vs No Contract

**gRPC:**
```protobuf
// user.proto - Mandatory contract
service UserService {
  rpc GetUser (GetUserRequest) returns (UserResponse);
}
```
✅ If you change the contract, code won't compile  
✅ Type safety at compile-time

**REST:**
```java
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) { ... }
```
⚠️ No formal contract  
⚠️ Errors appear at runtime

### 2. Binary vs Textual Serialization

**Protobuf (gRPC):**
```
Bytes: 08 01 12 06 55 73 65 72 20 31 1a 13 75 73 65 72 31 40 ...
Size: 68 bytes
Parsing: Direct to Java object
```

**JSON (REST):**
```json
{"id":1,"name":"User 1","email":"user1@example.com","age":21}
Size: 487 bytes
Parsing: String → Parser → HashMap → Object (slow)
```

### 3. HTTP/2 vs HTTP/1.1

**HTTP/2 (gRPC):**
- ✅ Multiplexing (multiple requests in 1 connection)
- ✅ Header compression (HPACK)
- ✅ Native bidirectional streaming

**HTTP/1.1 (REST):**
- ❌ 1 request per connection
- ❌ Uncompressed repeated headers
- ❌ No native streaming

---

## 🐛 Troubleshooting

### "Port already in use"

```bash
# Kill process on port 8080
lsof -i :8080
kill -9 <PID>

# Or change port in application.yml
```

### "protoc not found"

```bash
# Recompile to download protoc automatically
mvn clean compile -U
```

### "grpcurl not found"

```bash
# Install grpcurl (see Useful Tools section)
brew install grpcurl  # macOS
```

---

## 📈 Next Steps

1. **Understood the basics?** ✅
   - Compare REST vs gRPC code
   - Run and test both

2. **Want to measure performance?**
   - Use `ab` for REST
   - Use `ghz` for gRPC
   - Compare results

3. **Want to add features?**
   - Add authentication
   - Implement caching
   - Add validation

4. **Want to scale?**
   - Add Docker
   - Configure Kubernetes
   - Implement load balancer

---

## 📚 Resources

- [Complete Article](link-to-article) - Detailed technical analysis
- [gRPC Java Docs](https://grpc.io/docs/languages/java/)
- [Protocol Buffers Guide](https://protobuf.dev/)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)

---

## 🤝 Contributing

Found a bug? Have a suggestion? Open an issue!

---

## 📄 License

MIT License - Use as you wish!

---

## 💡 Final Tip

> **Start simple, understand the concepts, then add complexity.**

This project is deliberately minimalist to facilitate learning. Once you understand the basics well, explore the [complete repository with benchmarks](https://github.com/YaraLOliveira/grpc-vs-rest-java-benchmark) for more advanced use cases.

---

**Happy Coding!** 🚀
