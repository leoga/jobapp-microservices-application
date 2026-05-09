#!/bin/bash

set -e

# =========================
# 0. Args parsing
# =========================

MODE="docker"
SKIP_BUILD=false
FORCE_BUILD=false

# Primer argumento: modo (docker/jib)
if [[ "$1" == "docker" || "$1" == "jib" ]]; then
  MODE=$1
  shift
fi

# Flags adicionales
for arg in "$@"; do
  case $arg in
    --skipbuild)
      SKIP_BUILD=true
      shift
      ;;
    --forcebuild)
      FORCE_BUILD=true
      shift
      ;;
    *)
      echo "❌ Unknown argument: $arg"
      exit 1
      ;;
  esac
done

echo "🚀 Starting JobApp environment..."
echo "Mode: $MODE"
echo "Skip build: $SKIP_BUILD"
echo "Force build: $FORCE_BUILD"

# =========================
# 1. Prerequisites
# =========================
echo "🔍 Checking prerequisites..."

command -v kubectl >/dev/null || { echo "kubectl not installed"; exit 1; }
command -v helm >/dev/null || { echo "helm not installed"; exit 1; }
command -v docker >/dev/null || { echo "docker not installed"; exit 1; }
command -v minikube >/dev/null || { echo "minikube not installed"; exit 1; }

# =========================
# 2. Minikube
# =========================
echo "🐳 Checking Minikube..."

if ! minikube status | grep -q "Running"; then
  echo "⚠️ Minikube not running. Starting..."
  minikube start
fi

kubectl config use-context minikube
eval $(minikube docker-env)

# =========================
# 3. Image mapping
# =========================
declare -A IMAGES

IMAGES["configserver"]="leogatf/jobapp-config-server"
IMAGES["gateway"]="leogatf/jobapp-gateway"
IMAGES["company"]="leogatf/company"
IMAGES["job"]="leogatf/job"
IMAGES["review"]="leogatf/review"

SERVICES=("configserver" "gateway" "company" "job" "review")

# =========================
# 4. Build images
# =========================
image_exists() {
  docker image inspect "$1:latest" >/dev/null 2>&1
}

if [ "$SKIP_BUILD" = false ]; then
  echo "🔨 Building images..."

  cd ../..

  for s in "${SERVICES[@]}"; do
    IMAGE=${IMAGES[$s]}

    echo ""
    echo "👉 Service: $s"
    echo "   Image:   $IMAGE"

    if [ "$FORCE_BUILD" = false ] && image_exists "$IMAGE"; then
      echo "   ✔ Image exists → skipping"
      continue
    fi

    if [ "$FORCE_BUILD" = true ]; then
      echo "   ♻️ Force build enabled → rebuilding"
    else
      echo "   🔨 Building..."
    fi

    cd $s

    if [ "$MODE" == "docker" ]; then
      ./mvnw clean compile jib:dockerBuild
    else
      ./mvnw clean compile jib:build
    fi

    cd ..
  done

  cd deploy/k8s

else
  echo "⏭ Skipping build phase"
fi

# =========================
# 5. Namespaces
# =========================
echo "📦 Creating namespaces..."

kubectl create namespace jobapp || true

# =========================
# 6. Helm repos
# =========================
echo "📦 Updating Helm repos..."

helm repo add bitnami https://charts.bitnami.com/bitnami || true

helm repo update

# =========================
# 7. Infra (Postgres + Redis)
# =========================
echo "🧱 Deploying infrastructure..."

helm upgrade --install postgres bitnami/postgresql \
  -n jobapp \
  -f helm/infra/Postgres-values.yaml

helm upgrade --install redis bitnami/redis \
  -n jobapp

# =========================
# 8. Kafka + Keycloak (custom chart)
# =========================
echo "🧩 Deploying Kafka + Keycloak..."

helm upgrade --install infra helm/infra -n jobapp

# =========================
# 9. Microservices
# =========================
echo "🚀 Deploying microservices..."

helm upgrade --install jobapp helm/jobapp -n jobapp

# =========================
# 10. Wait for pods
# =========================
echo "⏳ Waiting for pods..."

kubectl wait --for=condition=ready pod --all -n jobapp --timeout=300s || true

# =========================
# 11. Port forwarding
# =========================
echo "🌐 Starting port-forward..."

kubectl port-forward svc/keycloak 8443:8080 -n jobapp > portforward.log 2>&1 &
kubectl port-forward svc/gateway 8080:80 -n jobapp > portforward.log 2>&1 &

echo ""
echo "✅ Environment ready:"
echo "👉 Gateway:   http://localhost:8080"
echo "👉 Keycloak:  http://localhost:8443"