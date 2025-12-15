# RCA — Pourquoi le backend crashait (Kafka) et comment on l’a stabilisé

## Problème 1 — DNS Kafka absent
### Symptôme
Crash :
- `Couldn't resolve server kafka:9092 ... DNS resolution failed`
- puis `No resolvable bootstrap urls given`

### Cause
`KAFKA_BOOTSTRAP_SERVERS=kafka:9092` mais **pas de Service `kafka`** dans `bpce-dev`.

### Action faite (workaround)
Création d’un **Service `kafka` vide** pour débloquer le démarrage :
```bash
cat <<'YAML' | oc -n bpce-dev apply -f -
apiVersion: v1
kind: Service
metadata:
  name: kafka
spec:
  ports:
  - name: tcp-kafka
    port: 9092
    targetPort: 9092
YAML
```

### Résultat
L’app démarre mais loggue des warnings (normal, pas de broker).

## Problème 2 — Config Reactive Messaging invalide
### Symptôme
`connector attribute must be set for channel 'events'`

### Cause
Le channel est déclaré mais `mp.messaging.*.<channel>.connector` était absent/cassé.

## Problème 3 — Conflit de noms (incoming + outgoing)
### Symptôme
`channel names cannot be used for both incoming and outgoing: [events]`

### Cause
Même nom `events` utilisé pour incoming et outgoing.

## État actuel
Pod backend RUNNING mais Kafka non déployé.
Le reste à faire : déployer AMQ Streams/Strimzi + Kafka cluster + topics.
