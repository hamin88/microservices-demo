#!/bin/sh
set -e

echo "Applying Kubernetes manifests..."
kubectl apply -k .

echo "Deployment complete."

echo "To access the login UI, add to /etc/hosts:"
echo "  127.0.0.1 microservices-demo.local"
echo "Then open http://microservices-demo.local"
