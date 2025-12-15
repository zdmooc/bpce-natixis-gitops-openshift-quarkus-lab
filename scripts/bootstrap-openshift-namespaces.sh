#!/usr/bin/env bash
set -euo pipefail

for ns in bpce-dev bpce-build bpce-preprod bpce-prod; do
  oc get ns "$ns" >/dev/null 2>&1 || oc new-project "$ns"
done

echo "Namespaces ready."
