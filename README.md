# BPCE/Natixis — GitOps OpenShift + Quarkus Lab (end‑to‑end)

Repo pédagogique “prêt à pratiquer” qui couvre :
- **Java/Quarkus** (API + tests unitaires & intégration)
- **Kubernetes/OpenShift** (manifests, Route, Health/Probes, resources)
- **GitOps** avec **Argo CD / OpenShift GitOps**
- **Kustomize** (base + overlays par environnement)
- **Helm** (chart applicatif + values par environnement)
- **CI/CD** (GitHub Actions + GitLab CI + Jenkinsfile – au choix)
- **Observabilité** (OpenTelemetry + traces/logs) + hooks de diagnostic
- **Kafka** (optionnel) via Reactive Messaging (prod/cons)

> Objectif : te permettre de simuler la mission “équipe conteneur” en mode portfolio.

---

## 0) Prérequis

- JDK **17** (ou 21)
- Maven 3.9+
- Podman ou Docker
- `oc` (OpenShift CLI)
- Un cluster **OpenShift Local (CRC)** ou OpenShift 4.x
- Argo CD **ou** OpenShift GitOps (Operator)

---

## 1) Quick start local (sans cluster)

### Backend Quarkus
```bash
cd apps/backend
./mvnw test
./mvnw quarkus:dev
# API : http://localhost:8080/api/hello
# Health : http://localhost:8080/q/health
```

### Kafka + OTel (optionnel) en docker-compose
```bash
cd local
docker compose up -d
```

---

## 2) Déploiement “manuel” sur OpenShift (pour comprendre)

```bash
oc login ...
oc new-project bpce-dev
oc -n bpce-dev apply -k deploy/kustomize/overlays/dev
```

Vérifications :
```bash
oc -n bpce-dev get all
oc -n bpce-dev get route
oc -n bpce-dev describe deploy backend
oc -n bpce-dev logs deploy/backend
```

---

## 3) Déploiement GitOps (Argo CD / OpenShift GitOps)

### 3.1 Installer Argo CD (si nécessaire)
Sur OpenShift, l’option recommandée est **OpenShift GitOps** (Operator).
Sur un Argo CD “standalone”, tu peux utiliser `gitops/argocd/bootstrap/argocd-install.yaml` (namespace `argocd`).

### 3.2 Root App (App of Apps)
1) Fork/clone ce repo dans ton GitHub (public).
2) Modifie `gitops/argocd/root-app.yaml` : remplace `REPO_URL` par l’URL de ton repo.
3) Applique :
```bash
oc apply -f gitops/argocd/root-app.yaml -n openshift-gitops
```

Ensuite Argo CD synchronise les apps par environnement (dev/build/preprod/prod).

---

## 4) CI/CD (choisir 1)

- GitHub Actions : `.github/workflows/ci.yml`
- GitLab CI : `.gitlab-ci.yml`
- Jenkins : `ci/Jenkinsfile`

Livraison attendue :
- build + tests
- image container
- packaging (Helm/Kustomize)
- (option) update GitOps repo / promotion (à faire comme exercice)

---

## 5) Exercices (parcours complet)

Voir : `docs/WORKSHOP.md` (checklist + pas à pas + validations).

---

## Structure

- `apps/backend` : Quarkus API + tests + OTel + Kafka optionnel
- `tools/gitops-scaffold` : CLI Node.js (JS) pour générer overlays Kustomize
- `deploy/helm` : chart Helm (backend)
- `deploy/kustomize` : base + overlays (dev/build/preprod/prod)
- `gitops/argocd` : root app + ApplicationSet (multi-env)
- `local` : docker-compose (otel collector, jaeger, kafka)
- `docs` : architecture, runbooks, ADRs, workshop

---

## Licence
MIT (voir `LICENSE`).
