#!/bin/bash

set -e

echo "🛑 Stopping JobApp environment..."

# =========================
# 1. Kill port-forward
# =========================
echo "🔌 Stopping port-forward..."

taskkill //F //IM kubectl.exe 2>/dev/null || true

# =========================
# 2. Uninstall Helm releases
# =========================
echo "🧹 Removing Helm releases..."

# Jobapp
helm uninstall jobapp -n jobapp || true
helm uninstall infra -n jobapp || true
helm uninstall postgres -n jobapp || true
helm uninstall redis -n jobapp || true

# Monitoring
helm uninstall monitoring -n monitoring || true
helm uninstall loki -n monitoring || true
helm uninstall promtail -n monitoring || true
helm uninstall zipkin -n monitoring || true

# =========================
# 3. Delete namespaces (optional)
# =========================
read -p "❓ Delete namespaces (jobapp, monitoring)? (y/n): " confirm

if [ "$confirm" = "y" ]; then
  kubectl delete namespace jobapp || true
  kubectl delete namespace monitoring || true
fi

echo "✅ Environment stopped"