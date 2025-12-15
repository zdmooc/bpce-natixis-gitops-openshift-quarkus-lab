# Runbook 04 — Validation & Troubleshooting

Backend :
```bash
oc -n bpce-dev get pods -l app=backend -o wide
P=$(oc -n bpce-dev get pods -l app=backend --sort-by=.metadata.creationTimestamp --no-headers | tail -n1 | awk '{print $1}')
oc -n bpce-dev logs "$P" --tail=200
oc -n bpce-dev logs "$P" --previous --tail=200 || true
oc -n bpce-dev describe pod "$P" | sed -n '/Events:/,$p' | tail -n 150
```

Kafka :
```bash
oc -n bpce-platform get pods,svc,endpoints
oc -n bpce-platform get kafkatopic
```

Env vars Quarkus :
```bash
oc -n bpce-dev set env deploy/backend --list | egrep -i 'KAFKA|MP_MESSAGING|QUARKUS' || true
```
