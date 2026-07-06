#!/bin/sh
set -e

if ! command -v kubectl >/dev/null 2>&1; then
  echo "kubectl is not installed. Install kubectl and configure a Kubernetes cluster before deploying."
  exit 1
fi

if ! kubectl cluster-info >/dev/null 2>&1; then
  echo "Cannot connect to Kubernetes cluster. Start a local cluster (minikube, Docker Desktop, kind) or set KUBECONFIG."
  exit 1
fi

echo "Applying Kubernetes manifests..."
kubectl apply -k .

echo "Deployment complete."

echo "Note: This manifest uses local images with imagePullPolicy: Never."
echo "Make sure your k3s containerd has the built images loaded or use a registry."
echo "To access the login UI, add to /etc/hosts:"
echo "  127.0.0.1 microservices-demo.local"
echo "Then open http://microservices-demo.local"
