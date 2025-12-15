# Dossier Architecture — BPCE Natixis GitOps (OpenShift + Argo CD + Quarkus + Kafka AMQ Streams/Strimzi)

Ce dossier est un **support d’architecture “réel”** (docs + schémas + manifests GitOps) pour déployer :
- **OpenShift** (CRC / cluster)
- **Argo CD (OpenShift GitOps)**
- **Quarkus backend** (container-tools-api)
- **Kafka** via **AMQ Streams (Strimzi Operator)**

## Ce que ce dossier apporte
1. **Schémas (Mermaid)** : contexte, déploiement, flux GitOps, flux Kafka.
2. **Manifests** : Argo CD (AppProject + App-of-Apps), Operator (AMQ Streams), Kafka cluster, KafkaTopic.
3. **Runbooks** : installation, validation, troubleshooting (avec commandes commentées).
4. **RCA du “problème Kafka”** : pourquoi ça crashait, ce qui a été patché, et ce qu’il reste à faire.

## Vue rapide (à adapter)
- Namespace App : `bpce-dev`
- Namespace Kafka : `bpce-platform` (recommandé)
- Namespace Operators : `openshift-operators`
- Namespace Argo CD : `openshift-gitops`

## Important (state actuel)
Pendant le debug, un **Service “kafka” vide** a été créé dans `bpce-dev` uniquement pour faire passer le démarrage (DNS OK, mais pas de broker derrière).

➡️ Dès que tu déploies un vrai Kafka Strimzi/AMQ Streams, il faut :
- supprimer ce service “fake”
- pointer le backend vers le **bootstrap service Strimzi** (ex: `bpce-kafka-kafka-bootstrap.bpce-platform.svc:9092`)

Voir :
- `runbooks/03-cleanup-fake-kafka-service.md`
- `runbooks/03-deploy-kafka-and-wire-backend.md`
