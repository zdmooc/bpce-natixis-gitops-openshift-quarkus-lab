# Sécurité (checklist)

- [ ] requests/limits CPU & mémoire définis
- [ ] probes (startup/readiness/liveness)
- [ ] exécuter en non-root (si possible)
- [ ] secrets via Secret (pas en clair)
- [ ] RBAC minimal (ServiceAccount)
- [ ] images taguées et traçables
- [ ] policy-as-code (bonus : Kyverno / Gatekeeper)
