#!/bin/bash

# Script to run REST Service

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  🔵 Starting REST Service"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📄 Protocol: HTTP/1.1"
echo "📦 Serialization: JSON (Jackson)"
echo "🌐 Port: 8080"
echo ""
echo "Endpoints:"
echo "  GET    http://localhost:8080/api/users/1"
echo "  GET    http://localhost:8080/api/users?limit=10"
echo "  POST   http://localhost:8080/api/users"
echo "  GET    http://localhost:8080/api/users/health"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check if target directory exists, if not compile
if [ ! -d "target/classes" ]; then
    echo "📦 Compiling project..."
    mvn clean compile
    echo ""
fi

# Run REST Application
echo "🚀 Launching REST Service..."
echo ""

mvn exec:java -Dexec.mainClass="com.starter.RestApp"