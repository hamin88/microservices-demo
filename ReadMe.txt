#Build the project
mvn clean install

#Start the Eureka Server (Infrastructure)
cd eureka-server
mvn spring-boot:run


#Start the Microservices

cd student-service
mvn spring-boot:run


cd user-service
mvn spring-boot:run


#Verify Registration for STUDENT-SERVICE and UEER-SERVICE

http://localhost:8761

#Test the Endpoints

Student List: http://localhost:8081/api/students

User List: http://localhost:8082/api/users


Open the gateway login page:
http://localhost:8080

Only this demo user can log in:
username: admin
password: admin

Build the React login UI:
cd login-ui
npm install
npm run build

The UI build writes static files into gateway-service/src/main/resources/static.

curl -X POST "http://localhost:8180/realms/microservices-demo/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded"  -d "client_id=gateway-client" -d "username=admin" -d "password=admin" -d "grant_type=password"

curl http://localhost:8080/api/students  -H "Authorization: Bearer <access_token>"



