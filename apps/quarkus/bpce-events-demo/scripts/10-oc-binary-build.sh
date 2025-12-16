#!/usr/bin/env bash
set -euo pipefail

NS="${NS:-bpce-platform}"
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "NS=$NS"
echo "APP_DIR=$APP_DIR"

command -v oc >/dev/null || { echo "ERROR: oc not found"; exit 1; }
command -v mvn >/dev/null || { echo "ERROR: mvn not found"; exit 1; }

oc -n "$NS" get bc bpce-events-demo >/dev/null

cd "$APP_DIR"
mvn -DskipTests package
oc -n "$NS" start-build bpce-events-demo --from-dir=. --follow
oc -n "$NS" get istag bpce-events-demo:latest -o wide
