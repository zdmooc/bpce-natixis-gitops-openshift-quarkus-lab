# Workshop — BPCE/Natixis GitOps/OpenShift/Quarkus

Objectif : reproduire **de bout en bout** le travail “équipe conteneur” :
- conception → dev → tests → doc → packaging → CI/CD → déploiement GitOps → observabilité → (option) Kafka

---

## A) Setup repo (portfolio)

1. Créer un repo GitHub public (ex: `bpce-natixis-gitops-lab`)
2. Push ce contenu
3. Compléter :
   - `docs/PROFILE.md`
   - `docs/ARCHITECTURE.md`
   - `docs/RUNBOOKS.md`

---

## B) Dev Quarkus (backend)

### B1 — Build & tests
```bash
cd apps/backend
./mvnw -q test
```

**Validation :**
- Les tests passent
- `target/surefire-reports` contient les rapports

### B2 — API endpoints
- `GET /api/hello`
- `GET /api/tools/info`
- `GET /q/health`
- `GET /q/metrics`

**Validation :**
- `curl -s localhost:8080/api/hello`
- health UP

---

## C) Packaging Kubernetes/OpenShift

### C1 — Kustomize overlays
```bash
oc new-project bpce-dev
oc -n bpce-dev apply -k deploy/kustomize/overlays/dev
```

**Validation :**
- Deployment READY
- Route créée
- Resources requests/limits présents

### C2 — Helm chart
```bash
helm lint deploy/helm/backend
helm template backend deploy/helm/backend -f deploy/helm/env/dev-values.yaml | head
```

**Validation :**
- rendu YAML OK

---

## D) GitOps Argo CD (multi‑env)

### D1 — Root app
- Mettre ton URL dans `gitops/argocd/root-app.yaml`
- Appliquer dans le namespace Argo (ex: `openshift-gitops`)

**Validation :**
- ArgoCD voit Root + Apps dev/build/preprod/prod
- Sync OK (ou manuel)

### D2 — Drift & self-heal
- Modifier un champ live (ex: replicas)
- Vérifier qu’Argo ramène l’état attendu (self-heal)

---

## E) CI/CD

### E1 — GitHub Actions
- Créer des secrets (ex : `REGISTRY`, `REGISTRY_USER`, `REGISTRY_PASSWORD`)
- Lancer pipeline

**Validation :**
- tests OK
- image build OK

### E2 — GitLab/Jenkins (option)
- Adapter les variables d’environnement
- Rejouer

---

## F) Observabilité (OpenTelemetry)

### F1 — Local stack
```bash
cd local
docker compose up -d
```

### F2 — Backend (OTLP)
- Export vers `otel-collector` (voir `apps/backend/src/main/resources/application.properties`)

**Validation :**
- Traces visibles dans Jaeger (http://localhost:16686)

---

## G) Kafka (optionnel)

### G1 — Local Kafka
Inclus dans `local/docker-compose.yml`.

### G2 — Producer/Consumer
Endpoints :
- `POST /api/kafka/publish?key=...` body texte
- Consumer logs côté backend

**Validation :**
- message reçu (logs)
- pas d’erreur de connexion

---

## H) “Livrables mission”
À produire dans ton repo (à pousser au fur et à mesure) :
- Code source + tests
- Documentation technique & produit
- Manifests Kustomize + chart Helm
- Pipelines CI/CD
- Runbooks de MEP + troubleshooting
- (bonus) ADRs, diagrammes Mermaid, conventions GitOps

Voir : `docs/DELIVERABLES.md`
