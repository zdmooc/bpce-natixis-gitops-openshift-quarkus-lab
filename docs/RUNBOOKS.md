# Runbooks (exemples)

## 1) Déployer en dev (Kustomize)
```bash
oc new-project bpce-dev
oc -n bpce-dev apply -k deploy/kustomize/overlays/dev
oc -n bpce-dev rollout status deploy/backend
oc -n bpce-dev get route backend -o jsonpath='{.spec.host}'; echo
```

## 2) Déployer via Argo CD
- Root app : `gitops/argocd/root-app.yaml`
- Vérifier Sync/Health

## 3) Incident : pod CrashLoopBackOff
```bash
oc -n bpce-dev get po
oc -n bpce-dev describe po <pod>
oc -n bpce-dev logs <pod> --previous
```

## 4) Incident : 503 sur Route
- vérifier Service endpoints
- vérifier readinessProbe

## 5) Diagnostic OTel
- exporter OTLP vers collector
- vérifier Jaeger (local) ou stack cible
