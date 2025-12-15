# Images / Registry

Tu as 2 options simples pour pratiquer.

## Option 1 — Registry externe (Quay/DockerHub)
1) Build
```bash
cd apps/backend
./mvnw -q package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t quay.io/<user>/container-tools-api:dev .
docker push quay.io/<user>/container-tools-api:dev
```

2) Mettre à jour l’image dans :
- `deploy/kustomize/base/deployment.yaml` (ou patches env)
- `deploy/helm/backend/values.yaml`

## Option 2 — Registry interne OpenShift (CRC)
1) Activer la route (si pas déjà fait) :
```bash
oc patch configs.imageregistry.operator.openshift.io/cluster --type=merge -p '{"spec":{"defaultRoute":true}}'
oc -n openshift-image-registry get route default-route
```

2) Login podman/docker :
```bash
REG=$(oc -n openshift-image-registry get route default-route -o jsonpath='{.spec.host}')
TOKEN=$(oc whoami -t)
podman login -u kubeadmin -p "$TOKEN" "$REG"
```

3) Push dans un namespace cible :
```bash
NS=bpce-dev
IMG="$REG/$NS/container-tools-api:dev"

cd apps/backend
./mvnw -q package -DskipTests
podman build -f src/main/docker/Dockerfile.jvm -t "$IMG" .
podman push "$IMG"
```

4) Mettre à jour l’image dans les manifests (ex: `deploy/kustomize/overlays/dev/patch-image.yaml`).

Voir aussi : `scripts/build-push-to-ocp-registry.sh`
