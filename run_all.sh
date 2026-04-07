#!/bin/bash

echo "Starting Microservices Ecosystem with Multiple Instances..."

cleanup() {
    echo ""
    echo "Stopping all services..."
    # Kill all child processes of this script
    pkill -P $$
    exit
}

trap cleanup SIGINT

echo "1. Building all projects..."
mvn clean install -DskipTests

echo "2. Starting Eureka Server..."
mvn spring-boot:run -pl eureka-server &

echo "Waiting for Eureka Server to be ready on port 8761..."
until curl -s http://localhost:8761 > /dev/null; do
  echo "Eureka is not ready yet... checking again in 2 seconds"
  sleep 2
done
echo "Eureka Server is UP!"

echo "3. Starting Gateway Service (Port 8080)..."
mvn spring-boot:run -pl gateway-service &

echo "4. Starting STUDENT-SERVICE Instances..."
# Instance 1 on Port 8081
mvn spring-boot:run -pl student-service -Dspring-boot.run.arguments="--server.port=8081 --eureka.instance.instance-id=student-instance-1" &
sleep 2
# Instance 2 on Random Port
mvn spring-boot:run -pl student-service -Dspring-boot.run.arguments="--server.port=0 --eureka.instance.instance-id=student-instance-2" &

echo "5. Starting USER-SERVICE Instances..."
# Instance 1 on Port 8082
mvn spring-boot:run -pl user-service -Dspring-boot.run.arguments="--server.port=8082 --eureka.instance.instance-id=user-instance-1" &
sleep 2
# Instance 2 on Random Port
mvn spring-boot:run -pl user-service -Dspring-boot.run.arguments="--server.port=0 --eureka.instance.instance-id=user-instance-2" &

echo "--------------------------------------------------------"
echo "Ecosystem is UP!"
echo "Eureka Dashboard: http://localhost:8761"
echo "Gateway Entry:    http://localhost:8080/api/users"
echo ""
echo "Note: It may take up to 30 seconds for all instances to"
echo "appear in the Eureka UI after they finish starting."
echo "Press Ctrl+C to stop all."
echo "--------------------------------------------------------"
wait
