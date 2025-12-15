# ADR-0001 — Choix Kafka via AMQ Streams / Strimzi (Operator)

## Statut
Accepté

## Contexte
Le backend Quarkus utilise SmallRye Reactive Messaging Kafka (channels `events` / `events-in`).
Un broker Kafka est requis pour produire/consommer.

## Décision
Déployer Kafka sur OpenShift via un Operator :
- **AMQ Streams** (Red Hat) si disponible dans OperatorHub
- sinon **Strimzi community operator**

## Conséquences
- namespace Kafka dédié (`bpce-platform`)
- aligner `bootstrap.servers` sur le service Strimzi `<cluster>-kafka-bootstrap`
