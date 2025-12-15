# Runbook 02 — Installer l’Operator AMQ Streams (GitOps)

Manifests :
- `kafka/operator/amq-streams/subscription.yaml`
- `kafka/operator/amq-streams/operatorgroup.yaml`

```bash
oc apply -f kafka/operator/amq-streams/

oc -n openshift-operators get sub,ip,csv | egrep -i 'amq|streams|kafka'
oc -n openshift-operators get pods | egrep -i 'amq|streams|strimzi|kafka'

oc get crd | egrep -i 'kafka\.strimzi|kafkatopic|kafkauser'
```

Note : ajuste `spec.channel` et `spec.source` après Runbook 01.
