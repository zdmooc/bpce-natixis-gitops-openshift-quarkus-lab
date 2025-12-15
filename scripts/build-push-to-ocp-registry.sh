#!/usr/bin/env bash
set -euo pipefail

NS="${1:-bpce-dev}"
TAG="${2:-dev}"

echo "[1/5] Ensure OpenShift internal registry route is enabled"
oc patch configs.imageregistry.operator.openshift.io/cluster --type=merge -p '{"spec":{"defaultRoute":true}}' >/dev/null

REG="$(oc -n openshift-image-registry get route default-route -o jsonpath='{.spec.host}')"
TOKEN="$(oc whoami -t)"
IMG="${REG}/${NS}/container-tools-api:${TAG}"

echo "[2/5] Login to registry: ${REG}"
podman login -u kubeadmin -p "${TOKEN}" "${REG}"

echo "[3/5] Build Quarkus jar"
cd apps/backend
./mvnw -q package -DskipTests

echo "[4/5] Build image: ${IMG}"
podman build -f src/main/docker/Dockerfile.jvm -t "${IMG}" .

echo "[5/5] Push image"
podman push "${IMG}"

echo "DONE: ${IMG}"
echo "Now update deploy/kustomize/overlays/${TAG}/patch-image.yaml (or Helm values) to use this image."
