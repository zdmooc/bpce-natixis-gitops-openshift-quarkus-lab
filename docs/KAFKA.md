# Kafka (optionnel)

Le backend utilise SmallRye Reactive Messaging :
- outgoing channel `events` -> topic `bpce.events`
- incoming channel `events-in` <- topic `bpce.events`

## Local
Voir `local/docker-compose.yml`.

## OpenShift
Recommandé : **AMQ Streams / Strimzi** (Operator).
Ensuite configurer `KAFKA_BOOTSTRAP_SERVERS` côté backend.
