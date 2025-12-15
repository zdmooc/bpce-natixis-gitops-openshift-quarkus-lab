# Runbook 01 — Découverte OperatorHub (AMQ Streams / Strimzi)

Objectif : récupérer le **channel** et le **catalog source** corrects.

```bash
oc get ns openshift-marketplace openshift-operators

# lister les packages kafka
oc -n openshift-marketplace get packagemanifests | egrep -i 'amq|streams|strimzi|kafka'

# inspect AMQ Streams
oc -n openshift-marketplace get packagemanifests amq-streams -o yaml | sed -n '1,200p'

# alternative Strimzi community
oc -n openshift-marketplace get packagemanifests strimzi-kafka-operator -o yaml | sed -n '1,200p' || true

# vérifier CRDs (si déjà installé)
oc get crd | egrep -i 'kafka\.strimzi|kafkatopic|kafkauser' || true
```
