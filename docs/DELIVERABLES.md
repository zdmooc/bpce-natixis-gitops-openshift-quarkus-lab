# Livrables (format “mission”)

## 1) Code source
- Quarkus backend (`apps/backend`)
- CLI JS outillage (`tools/gitops-scaffold`)

## 2) Tests
- Unitaires (JUnit)
- Intégration (RestAssured)

## 3) Documentation
- `docs/ARCHITECTURE.md`
- `docs/RUNBOOKS.md`
- `docs/SECURITY.md`
- `docs/OPERATIONS.md`

## 4) Packaging
- Kustomize : `deploy/kustomize`
- Helm : `deploy/helm`

## 5) CI/CD
- GitHub Actions : `.github/workflows/ci.yml`
- GitLab : `.gitlab-ci.yml`
- Jenkins : `ci/Jenkinsfile`

## 6) Mise en production (GitOps)
- Argo CD : `gitops/argocd`

## 7) Observabilité
- OpenTelemetry + Jaeger local : `local/`
