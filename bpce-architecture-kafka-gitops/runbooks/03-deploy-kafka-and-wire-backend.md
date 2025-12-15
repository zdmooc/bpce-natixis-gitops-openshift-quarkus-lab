# Runbook 03 — Déployer Kafka (cluster + topic) et câbler le backend

```bash
# namespace kafka
oc new-project bpce-platform || true

# cluster kafka + topic
oc -n bpce-platform apply -f kafka/cluster/
oc -n bpce-platform apply -f kafka/topics/

# vérifier kafka READY + services bootstrap
oc -n bpce-platform get pods -w
oc -n bpce-platform get svc | egrep -i 'kafka|bootstrap'
```

Configurer le backend vers le bootstrap Strimzi (cluster = `bpce-kafka`) :
```bash
oc -n bpce-dev set env deploy/backend KAFKA_BOOTSTRAP_SERVERS=bpce-kafka-kafka-bootstrap.bpce-platform.svc:9092
oc -n bpce-dev rollout restart deploy/backend
```

Nettoyer le “fake” service kafka si existant :
```bash
oc -n bpce-dev delete svc kafka || true
```
