#!/bin/bash

set -e

# =========================
# 0. Args parsing
# =========================

MODE="docker"
FORCE_BUILD=false

# Usuario Docker Hub por defecto
DOCKERHUB_USER="${DOCKERHUB_USER:-leogatf}"

# Primer argumento: modo (docker/jib)
if [[ "$1" == "docker" || "$1" == "jib" ]]; then
  MODE=$1
  shift
fi

# Flags adicionales
for arg in "$@"; do
  case $arg in
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

# =========================
# Docker Hub user (solo jib)
# =========================

if [ "$MODE" == "jib" ]; then
  echo ""
  read -p "🐳 Docker Hub username [${DOCKERHUB_USER}]: " INPUT_USER

  if [ -n "$INPUT_USER" ]; then
    export DOCKERHUB_USER="$INPUT_USER"
  else
    export DOCKERHUB_USER="$DOCKERHUB_USER"
  fi

  echo "✔ Using Docker Hub user: $DOCKERHUB_USER"

  # =========================
  # Docker login check
  # =========================

  echo "🔐 Checking Docker Hub login..."

  if ! docker info 2>/dev/null | grep -q "Username"; then
    echo "⚠️ Not logged into Docker Hub"
    docker login
  else
    echo "✔ Docker Hub login detected"
  fi
fi

echo ""
echo "🚀 Starting JobApp environment..."
echo "Mode: $MODE"
echo "Docker Hub user: $DOCKERHUB_USER"
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

IMAGES["configserver"]="${DOCKERHUB_USER}/jobapp-config-server"
IMAGES["gateway"]="${DOCKERHUB_USER}/jobapp-gateway"
IMAGES["company"]="${DOCKERHUB_USER}/company"
IMAGES["job"]="${DOCKERHUB_USER}/job"
IMAGES["review"]="${DOCKERHUB_USER}/review"

SERVICES=("configserver" "gateway" "company" "job" "review")

# =========================
# 4. Build images
# =========================

image_exists() {
  docker image inspect "$1:latest" >/dev/null 2>&1
}

echo "🔨 Checking/building images..."

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
    echo "   🔨 Image not found → building"
  fi

  cd $s

  if [ "$MODE" == "docker" ]; then
    ./mvnw clean compile jib:dockerBuild \
      -Dimage="$IMAGE"
  else
    ./mvnw clean compile jib:build \
      -Dimage="$IMAGE"
  fi

  cd ..
done

cd deploy/k8s

# =========================
# 5. Namespaces
# =========================
echo "📦 Creating namespaces..."

kubectl create namespace jobapp || true
kubectl create namespace monitoring || true

# =========================
# 6. Helm repos
# =========================
echo "📦 Updating Helm repos..."

helm repo add bitnami https://charts.bitnami.com/bitnami || true
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts || true
helm repo add grafana https://grafana.github.io/helm-charts || true
helm repo add zipkin https://zipkin.io/zipkin-helm || true

helm repo update

# =========================
# 7. Infra (Postgres + Redis)
# =========================
echo "🧱 Deploying infrastructure..."

helm upgrade --install postgres bitnami/postgresql \
  -n jobapp \
  -f helm/infra/postgres-values.yaml

helm upgrade --install redis bitnami/redis \
  -n jobapp

# =========================
# 8. Kafka + Keycloak (custom chart)
# =========================
echo "🧩 Deploying Kafka + Keycloak..."

helm upgrade --install infra helm/infra -n jobapp

# =========================
# 9. Monitoring stack
# =========================
echo "📊 Deploying monitoring..."

helm upgrade --install loki grafana/loki \
  -f helm/monitoring/lokiValues.yaml \
  -n monitoring

helm upgrade --install promtail grafana/promtail \
  -n monitoring \
  --set config.clients[0].url=http://loki-gateway/loki/api/v1/push

helm upgrade --install zipkin zipkin/zipkin \
  -n monitoring

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  -f helm/monitoring/grafanaValues.yaml \
  -n monitoring

# =========================
# 9.1 ServiceMonitors
# =========================
echo "📡 Deploying ServiceMonitors..."

kubectl apply -f helm/monitoring/servicemonitors

# =========================
# 10. Microservices
# =========================
echo "🚀 Deploying microservices..."

helm upgrade --install jobapp helm/jobapp -n jobapp

# =========================
# 11. Wait for pods
# =========================
echo "⏳ Waiting for pods..."

kubectl wait --for=condition=ready pod --all -n jobapp --timeout=300s || true
kubectl wait --for=condition=ready pod --all -n monitoring --timeout=180s || true

# =========================
# 12. Port forwarding
# =========================
echo "🌐 Starting port-forward..."

kubectl port-forward svc/monitoring-grafana 3000:80 -n monitoring > portforward.log 2>&1 &
kubectl port-forward svc/keycloak 8443:8080 -n jobapp > portforward.log 2>&1 &
kubectl port-forward svc/gateway 8080:80 -n jobapp > portforward.log 2>&1 &

echo ""
echo "✅ Environment ready:"
echo "👉 Gateway:   http://localhost:8080"
echo "👉 Keycloak:  http://localhost:8443"
echo "👉 Grafana:   http://localhost:3000"