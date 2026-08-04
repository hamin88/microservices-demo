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
- file-ingestion-service (file ingestion service for ingesting files to s3)
- rule-engine (spring boot application which runs the rules using spring quartz)
- rule-metadata-service
- snowflake-data-service (snowflake data fetching service for reports)
- export-service (Export utility service for exporting data to excel/csv)
Work Flow :
- rule-engine triggers file-ingestion-service 
- file-ingestion-service -> get files from sftp server and storing them to s3 
  then triggers snowflake-data-service
- snowflake-data-service -> imports data to snowflake and 
  then triggers dataflow
- dataflow -> prepares data and exports to excel/csv using export-service

Kubernates :
- eks cluster
- istio (service mesh for routing and security)
- api gateway (with istio ingress gateway)
- deployment.yaml (for each microservice)
- service.yaml (with ClusterIP for each microservice)
- configmap.yaml (for storing configuration properties)
- ui application (react application for user interface)

GitHub Actions :
- build
- push docker image to ECR
  - aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.<region>.amazonaws.com
  - docker build -t <local_image_name> .
  - docker tag <local_image_name>:latest <aws_account_id>.dkr.ecr.<region>://<repository_name>:latest
  - docker push <aws_account_id>.dkr.ecr.<region>://<repository_name>:latest
- deploy to kubernetes EKS cluster
  - aws eks update-kubeconfig --region <region> --name <cluster_name>
  - kubectl apply -f deployment.yaml