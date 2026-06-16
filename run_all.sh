#!/bin/sh
set -eu

echo "Starting Microservices Ecosystem..."

PIDS=""

cleanup() {
    echo ""
    echo "Stopping all services..."
    if [ -n "$PIDS" ]; then
        kill $PIDS 2>/dev/null || true
    fi
    exit
}

trap cleanup INT TERM

if ! command -v java >/dev/null 2>&1; then
    echo "java was not found. Install Java 17 or newer on this server." >&2
    exit 1
fi

find_jar() {
    service_dir="$1"
    jar_path="dist/$service_dir.jar"

    if [ -f "$jar_path" ]; then
        printf '%s\n' "$jar_path"
        return
    fi

    jar_path="$service_dir/target/$service_dir-1.0-SNAPSHOT.jar"

    if [ ! -f "$jar_path" ]; then
        echo "Missing jar for $service_dir." >&2
        echo "Expected dist/$service_dir.jar or $service_dir/target/$service_dir-1.0-SNAPSHOT.jar." >&2
        echo "Build the project in CI and deploy the generated dist directory before running this script." >&2
        exit 1
    fi

    printf '%s\n' "$jar_path"
}

start_service() {
    service_name="$1"
    jar_path="$2"
    shift 2

    echo "Starting $service_name..."
    java -jar "$jar_path" "$@" &
    PIDS="$PIDS $!"
}

EUREKA_JAR="$(find_jar eureka-server)"
GATEWAY_JAR="$(find_jar gateway-service)"
STUDENT_JAR="$(find_jar student-service)"
USER_JAR="$(find_jar user-service)"

start_service "Eureka Server" "$EUREKA_JAR" \
    --server.address=0.0.0.0

echo "Waiting for Eureka Server to be ready..."
until curl -fsS http://localhost:8761 >/dev/null 2>&1; do
  sleep 2
done
echo "Eureka Server is UP!"

export EUREKA_SERVER_URL="${EUREKA_SERVER_URL:-http://localhost:8761/eureka/}"
export SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5432/userdb}"
export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-postgres}"
export SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-postgres}"
export JWT_ISSUER_URI="${JWT_ISSUER_URI:-http://localhost:8180/realms/microservices-demo}"
export JWT_JWK_SET_URI="${JWT_JWK_SET_URI:-http://localhost:8180/realms/microservices-demo/protocol/openid-connect/certs}"
export KEYCLOAK_BASE_URL="${KEYCLOAK_BASE_URL:-http://localhost:8180}"

start_service "Gateway Service" "$GATEWAY_JAR" \
    --server.address=0.0.0.0

echo "Starting STUDENT-SERVICE Instances..."
start_service "STUDENT-SERVICE student-1" "$STUDENT_JAR" \
    --server.address=0.0.0.0 \
    --server.port=8081 \
    --eureka.instance.instance-id=student-1
start_service "STUDENT-SERVICE student-2" "$STUDENT_JAR" \
    --server.address=0.0.0.0 \
    --server.port=0 \
    --eureka.instance.instance-id=student-2

echo "Starting USER-SERVICE Instances..."
start_service "USER-SERVICE user-1" "$USER_JAR" \
    --server.address=0.0.0.0 \
    --server.port=8082 \
    --eureka.instance.instance-id=user-1
start_service "USER-SERVICE user-2" "$USER_JAR" \
    --server.address=0.0.0.0 \
    --server.port=0 \
    --eureka.instance.instance-id=user-2

echo "--------------------------------------------------------"
echo "Ecosystem is UP!"
echo "Eureka: http://localhost:8761"
echo "Gateway: http://localhost:8080/api/users"
echo "--------------------------------------------------------"

wait
