# Runbook — Nettoyage du Service Kafka “fake”

```bash
oc -n bpce-dev get svc kafka -o wide || true
oc -n bpce-dev get endpoints kafka -o wide || true
oc -n bpce-dev delete svc kafka || true
```
