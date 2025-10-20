#!/bin/bash

# Script to test both REST and gRPC services

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  🧪 Testing REST vs gRPC"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# ==================== TEST 1: REST Service ====================

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  📋 TEST 1: REST Service"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check if REST service is running
if ! curl -s http://localhost:8080/api/users/health > /dev/null 2>&1; then
    echo -e "${RED}❌ REST Service is not running!${NC}"
    echo "   Please start it first: ./run-rest.sh"
    echo ""
else
    echo -e "${GREEN}✅ REST Service is running${NC}"
    echo ""

    # Test GET User
    echo "📌 GET User (ID: 1)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    START=$(date +%s%N)
    RESPONSE=$(curl -s http://localhost:8080/api/users/1)
    END=$(date +%s%N)
    REST_TIME=$(( ($END - $START) / 1000000 ))

    echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
    echo ""
    echo -e "${BLUE}⏱️  Latency: ${REST_TIME}ms${NC}"

    # Calculate payload size
    REST_SIZE=$(echo "$RESPONSE" | wc -c)
    echo -e "${BLUE}📦 Payload Size: ${REST_SIZE} bytes${NC}"
    echo ""

    # Test List Users
    echo "📌 LIST Users (limit: 3)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    curl -s "http://localhost:8080/api/users?limit=3" | python3 -m json.tool 2>/dev/null
    echo ""
fi

echo ""

# ==================== TEST 2: gRPC Service ====================

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  📋 TEST 2: gRPC Service"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check if grpcurl is installed
if ! command -v grpcurl &> /dev/null; then
    echo -e "${RED}❌ grpcurl not found!${NC}"
    echo ""
    echo "Install grpcurl:"
    echo "  macOS:  brew install grpcurl"
    echo "  Linux:  Download from https://github.com/fullstorydev/grpcurl/releases"
    echo ""
else
    # Check if gRPC service is running
    if ! grpcurl -plaintext localhost:9090 list &> /dev/null; then
        echo -e "${RED}❌ gRPC Service is not running!${NC}"
        echo "   Please start it first: ./run-grpc.sh"
        echo ""
    else
        echo -e "${GREEN}✅ gRPC Service is running${NC}"
        echo ""

        # Test GetUser
        echo "📌 GetUser (ID: 1)"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

        START=$(date +%s%N)
        grpcurl -plaintext -d '{"id": 1}' localhost:9090 user.UserService/GetUser
        END=$(date +%s%N)
        GRPC_TIME=$(( ($END - $START) / 1000000 ))

        echo ""
        echo -e "${GREEN}⏱️  Latency: ${GRPC_TIME}ms${NC}"
        echo -e "${GREEN}📦 Payload Size: ~68 bytes (binary)${NC}"
        echo ""

        # Test ListUsers with streaming
        echo "📌 ListUsers (Server Streaming - limit: 3)"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "⚡ Notice: Users are sent ONE BY ONE (streaming)"
        echo ""

        grpcurl -plaintext -d '{"limit": 3}' localhost:9090 user.UserService/ListUsers
        echo ""
    fi
fi

# ==================== COMPARISON ====================

if [ ! -z "$REST_TIME" ] && [ ! -z "$GRPC_TIME" ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  📊 COMPARISON"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    echo "Latency:"
    echo "  REST:  ${REST_TIME}ms"
    echo "  gRPC:  ${GRPC_TIME}ms"
    echo ""

    if [ $GRPC_TIME -lt $REST_TIME ]; then
        DIFF=$(( $REST_TIME - $GRPC_TIME ))
        IMPROVEMENT=$(awk "BEGIN {printf \"%.1f\", $REST_TIME/$GRPC_TIME}")
        echo -e "${GREEN}  ✓ gRPC was ${DIFF}ms faster (${IMPROVEMENT}x improvement)${NC}"
    else
        echo -e "${YELLOW}  ⚠️  Results may vary on single requests${NC}"
        echo "     Run load tests for accurate comparison"
    fi
    echo ""

    echo "Payload Size:"
    echo "  REST:  ${REST_SIZE} bytes (JSON)"
    echo "  gRPC:  ~68 bytes (Protobuf binary)"

    if [ ! -z "$REST_SIZE" ]; then
        REDUCTION=$(awk "BEGIN {printf \"%.1f\", (1 - 68/$REST_SIZE) * 100}")
        echo -e "${GREEN}  ✓ gRPC payload is ${REDUCTION}% smaller${NC}"
    fi
    echo ""

    echo "Streaming:"
    echo "  REST:  ❌ No native streaming"
    echo "  gRPC:  ✅ 4 types of streaming (Unary, Server, Client, Bidirectional)"
    echo ""
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "💡 Tips:"
echo "   • Use 'ab' for REST load testing"
echo "   • Use 'ghz' for gRPC load testing"
echo "   • Try modifying user.proto to see type safety!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""