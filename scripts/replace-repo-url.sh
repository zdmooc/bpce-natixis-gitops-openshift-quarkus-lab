#!/usr/bin/env bash
set -euo pipefail
REPO_URL="${1:-}"
if [[ -z "$REPO_URL" ]]; then
  echo "Usage: $0 https://github.com/<user>/<repo>.git"
  exit 1
fi

# Replace placeholders in Argo CD manifests
find gitops/argocd -type f -name '*.yaml' -print0 | xargs -0 sed -i "s|REPO_URL|$REPO_URL|g"
echo "Updated REPO_URL in gitops/argocd/*.yaml"
