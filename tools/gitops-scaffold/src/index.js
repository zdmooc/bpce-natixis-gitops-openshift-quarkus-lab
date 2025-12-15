import fs from "node:fs";
import path from "node:path";

function arg(name, def = null) {
  const i = process.argv.indexOf(name);
  if (i === -1) return def;
  return process.argv[i + 1] ?? def;
}

const appName = arg("--name", "backend");
const outDir = arg("--out", "./out");
const namespace = arg("--namespace", "bpce-dev");

const base = path.join(outDir, "base");
const overlays = path.join(outDir, "overlays");

function ensure(p) { fs.mkdirSync(p, { recursive: true }); }

function write(p, content) {
  ensure(path.dirname(p));
  fs.writeFileSync(p, content, { encoding: "utf-8" });
}

ensure(base);
ensure(overlays);

write(path.join(base, "kustomization.yaml"), `resources:
- deployment.yaml
- service.yaml
- route.yaml
`);

write(path.join(base, "deployment.yaml"), `apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${appName}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${appName}
  template:
    metadata:
      labels:
        app: ${appName}
    spec:
      containers:
      - name: ${appName}
        image: quay.io/CHANGE_ME/${appName}:dev
        ports:
        - containerPort: 8080
`);

write(path.join(base, "service.yaml"), `apiVersion: v1
kind: Service
metadata:
  name: ${appName}
spec:
  selector:
    app: ${appName}
  ports:
  - port: 80
    targetPort: 8080
`);

write(path.join(base, "route.yaml"), `apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: ${appName}
spec:
  to:
    kind: Service
    name: ${appName}
  port:
    targetPort: 80
`);

for (const env of ["dev", "build", "preprod", "prod"]) {
  const envDir = path.join(overlays, env);
  ensure(envDir);
  write(path.join(envDir, "kustomization.yaml"), `namespace: ${namespace}
resources:
- ../../base
patches:
- path: patch-replicas.yaml
`);
  write(path.join(envDir, "patch-replicas.yaml"), `apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${appName}
spec:
  replicas: ${env === "prod" ? 3 : 1}
`);
}

console.log(`Generated Kustomize skeleton for '${appName}' in ${outDir}`);
