Database : 
-localhost:10000/h2-console
-admin/admin123

Swagger :
-http://localhost:10000/swagger-ui/index.html
-http://localhost:10000/v3/api-docs


UI  modules :

Core Services :
- database-service (flyway migration service)
- common-service (logging,security filter, exception handling, etc)

Micoservices :
- api-gateway
- auth-service (calls keycloak for authentication and authorization)
- rule-engine (spring boot application which runs the rules using spring quartz)
- rule-metadata-service
- snowflake-date-service (snowflake data fetching service for reports)
- export-service (Export utility service for exporting data to excel/csv)
gitHubActions :
- build
- push docker image to ECR
- deploy to kubernetes EKS cluster