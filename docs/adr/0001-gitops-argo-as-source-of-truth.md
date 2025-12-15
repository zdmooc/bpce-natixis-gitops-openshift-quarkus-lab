# ADR 0001 — GitOps avec Argo CD comme source de vérité

## Status
Accepted

## Contexte
Nous voulons éviter les déploiements manuels et assurer la reproductibilité multi-environnements.

## Décision
Utiliser Argo CD (ou OpenShift GitOps) pour synchroniser l’état du cluster depuis le repo Git.
Les manifests sont gérés via Kustomize (overlays) et/ou Helm (chart + values).

## Conséquences
- Toute modification passe par PR (promotion)
- Self-heal et prune activés sur les apps critiques
