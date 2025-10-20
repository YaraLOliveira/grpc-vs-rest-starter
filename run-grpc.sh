#!/bin/bash

# Script to run gRPC Service

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  🟢 Starting gRPC Service"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "⚡ Protocol: HTTP/2"
echo "📦 Serialization: Protocol Buffers (binary)"
echo "🌐 Port: 9090"
echo ""
echo "Test with grpcurl:"
echo "  grpcurl -plaintext localhost:9090 list"
echo "  grpcurl -plaintext -d '{\"id\": 1}' localhost:9090 user.UserService/GetUser"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check if protobuf code was generated
if [ ! -d "target/generated-sources/protobuf" ]; then
    echo "⚡ Generating code from .proto file..."
    mvn clean compile
    echo ""
fi

# Run gRPC Application
echo "🚀 Launching gRPC Service..."
echo ""

mvn exec:java -Dexec.mainClass="com.starter.GrpcApp"