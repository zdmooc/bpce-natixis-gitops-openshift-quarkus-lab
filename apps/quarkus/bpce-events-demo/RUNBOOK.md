# RUNBOOK — bpce-events-demo (CRC / OpenShift Local)

## 0) Pré-requis
- oc connecté à CRC
- namespace: bpce-platform

## 1) Build + push image (Binary BuildConfig)
    NS=bpce-platform
    cd apps/quarkus/bpce-events-demo
    ./scripts/10-oc-binary-build.sh

## 2) Vérifier pods + route
    oc -n bpce-platform get pod,route | egrep 'bpce-events-demo|NAME'

## 3) Test fonctionnel (/events -> Kafka)
    NS=bpce-platform
    HOST=$(oc -n "$NS" get route bpce-events-demo -o jsonpath='{.spec.host}')
    MSG="demo:runbook-$(date +%Y%m%d-%H%M%S)"
    curl -sS -i --max-time 10 -X POST "http://$HOST/events" -H 'Content-Type: text/plain' --data "$MSG"
    oc -n "$NS" logs deploy/bpce-events-demo --since=2m | tail -n 200 | grep -F "$MSG"

## 4) Preuves attendues
- Argo: bpce-kafka-lab + bpce-events-demo-lab = Synced/Healthy
- Route: bpce-events-demo accessible
- Logs: bpce-events-demo received: <MSG>
