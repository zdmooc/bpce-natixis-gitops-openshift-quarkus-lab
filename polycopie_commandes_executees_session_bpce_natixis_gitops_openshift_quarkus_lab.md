# Polycopié — Commandes exécutées (session bpce-natixis-gitops-openshift-quarkus-lab)

> Document reconstitué **uniquement** à partir des blocs console collés dans cette conversation (pas d’accès au `history` complet de la machine).

Repo : https://github.com/zdmooc/bpce-natixis-gitops-openshift-quarkus-lab

---

## 0) Diagnostic Argo CD (sync/health + erreurs RBAC)

### 0.1 Statut Argo (résumé sync/health)
```bash
oc -n openshift-gitops get app bpce-kafka-lab bpce-events-demo-lab \
  -o jsonpath='{range .items[*]}{.metadata.name} sync={.status.sync.status} health={.status.health.status}{"\n"}{end}' || true
```
Objectif : voir rapidement `Synced/OutOfSync` et `Healthy/Progressing/Missing/Degraded`.

### 0.2 Détail d’erreur RBAC (forbidden)
```bash
oc -n openshift-gitops describe app bpce-kafka-lab | tail -n 80
oc -n openshift-gitops describe app bpce-events-demo-lab | tail -n 80
```
Objectif : identifier quels objets échouent (`Kafka`, `KafkaUser`, `KafkaNodePool`, `Route`, `BuildConfig`, etc.) et le message `forbidden`.

---

## 1) Config Quarkus Kafka (application.properties)

### 1.1 Localiser la ligne fautive (security.protocol)
```bash
grep -n "SECURITY_PROTOCOL\|security.protocol\|KAFKA_SECURITY_PROTOCOL" -n \
  apps/quarkus/bpce-events-demo/src/main/resources/application.properties
```
Objectif : trouver la déclaration responsable de l’échec Maven/Quarkus (mauvaise valeur / mauvais placeholder).

### 1.2 Tentative de correction (suppression + ajout de propriétés)
```bash
cd /c/workspaces/2026/bpce-natixis-gitops-openshift-quarkus-lab

# 1) supprimer la ligne fautive
sed -i '14d' apps/quarkus/bpce-events-demo/src/main/resources/application.properties

# 2) ajouter les 2 propriétés (incoming + outgoing) à la fin du fichier
cat <<'EOF' >> apps/quarkus/bpce-events-demo/src/main/resources/application.properties

# Kafka security protocol (SASL SCRAM)
mp.messaging.outgoing.bpce-events.security.protocol=${env:KAFKA_SECURITY_PROTOCOL:SASL_PLAINTEXT}
mp.messaging.incoming.bpce-events.security.protocol=${env:KAFKA_SECURITY_PROTOCOL:SASL_PLAINTEXT}
EOF

# 3) vérifier
tail -n 30 apps/quarkus/bpce-events-demo/src/main/resources/application.properties
```
Objectif : tentative de forcer le protocole SASL.

### 1.3 Correction finale (placeholders Quarkus + propriété attendue par le client Kafka)
```bash
cd /c/workspaces/2026/bpce-natixis-gitops-openshift-quarkus-lab

F=apps/quarkus/bpce-events-demo/src/main/resources/application.properties

# 1) corriger la syntaxe des placeholders (Quarkus: ${VAR:default}, pas ${env:VAR:default})
sed -i 's/${env:KAFKA_BOOTSTRAP_SERVERS}/${KAFKA_BOOTSTRAP_SERVERS:bpce-kafka-kafka-bootstrap:9093}/' "$F"
sed -i 's/${env:KAFKA_SASL_MECHANISM:SCRAM-SHA-512}/${KAFKA_SASL_MECHANISM:SCRAM-SHA-512}/' "$F"
sed -i 's/${env:KAFKA_USERNAME}/${KAFKA_USERNAME}/' "$F"
sed -i 's/${env:KAFKA_PASSWORD}/${KAFKA_PASSWORD}/' "$F"

# 2) supprimer les 2 lignes "bpce-events.security.protocol" (mauvais nom de channel)
sed -i '/^mp\.messaging\.outgoing\.bpce-events\.security\.protocol=/d' "$F"
sed -i '/^mp\.messaging\.incoming\.bpce-events\.security\.protocol=/d' "$F"

# 3) ajouter la propriété attendue par le client Kafka (avec default build-safe)
grep -q '^kafka.security.protocol=' "$F" || \
  sed -i '/^kafka\.sasl\.jaas\.config=/a kafka.security.protocol=${KAFKA_SECURITY_PROTOCOL:SASL_PLAINTEXT}' "$F"

# 4) preuve
nl -ba "$F" | tail -n 60

# 5) commit + push (uniquement ce fichier)
git add "$F"
git commit -m "fix: quarkus kafka config placeholders + security protocol"
git push
```
Objectif : rendre la config compatible Quarkus + Kafka client, éviter l’erreur sur `SecurityProtocol`.

---

## 2) Namespace GitOps (label managed-by)

```bash
# 1) label "managed-by" (débloque certains droits Argo sur le namespace)
oc label ns bpce-platform argocd.argoproj.io/managed-by=openshift-gitops --overwrite

# 2) vérif
oc get ns bpce-platform --show-labels | tr ',' '\n' | egrep 'bpce-platform|argocd.argoproj.io/managed-by|pod-security' || true
```
Objectif : aligner le namespace avec la gestion GitOps.

---

## 3) RBAC Argo (donner admin au controller dans le namespace)

```bash
# 1) donner les droits "admin" au controller Argo dans le namespace (lab)
oc adm policy add-role-to-user admin \
  -z openshift-gitops-argocd-application-controller \
  -n bpce-platform

# 2) preuve: Argo a maintenant le droit de patch un CR Strimzi
oc -n bpce-platform auth can-i patch kafkas.kafka.strimzi.io \
  --as=system:serviceaccount:openshift-gitops:openshift-gitops-argocd-application-controller
```
Objectif : supprimer les erreurs `forbidden` (create/patch) côté Argo.

### 3.1 Vérification exhaustive des droits (create/patch)
```bash
SA=system:serviceaccount:openshift-gitops:openshift-gitops-argocd-application-controller
NS=bpce-platform

oc -n $NS auth can-i create deployments.apps --as=$SA
oc -n $NS auth can-i create routes.route.openshift.io --as=$SA
oc -n $NS auth can-i create buildconfigs.build.openshift.io --as=$SA
oc -n $NS auth can-i create imagestreams.image.openshift.io --as=$SA
oc -n $NS auth can-i patch kafkas.kafka.strimzi.io --as=$SA
oc -n $NS auth can-i patch kafkanodepools.kafka.strimzi.io --as=$SA
oc -n $NS auth can-i patch kafkatopics.kafka.strimzi.io --as=$SA
oc -n $NS auth can-i patch kafkausers.kafka.strimzi.io --as=$SA
```
Objectif : confirmer que tout retourne `yes`.

---

## 4) Forcer Argo (refresh/sync)

### 4.1 Refresh hard
```bash
oc -n openshift-gitops annotate app bpce-kafka-lab argocd.argoproj.io/refresh=hard --overwrite
oc -n openshift-gitops annotate app bpce-events-demo-lab argocd.argoproj.io/refresh=hard --overwrite
```
Objectif : forcer la re-comparaison source/destination.

### 4.2 Force sync via patch operation.sync
```bash
# Force sync Kafka app
oc -n openshift-gitops patch app bpce-kafka-lab --type=merge \
  -p '{"operation":{"sync":{"prune":true}}}'

# Force sync Demo app
oc -n openshift-gitops patch app bpce-events-demo-lab --type=merge \
  -p '{"operation":{"sync":{"prune":true}}}'
```
Objectif : déclencher une synchronisation immédiate.

### 4.3 Vérifier sync/health/phase/message
```bash
oc -n openshift-gitops get app bpce-kafka-lab bpce-events-demo-lab \
  -o jsonpath='{range .items[*]}{.metadata.name} sync={.status.sync.status} health={.status.health.status} phase={.status.operationState.phase}{"\n"}{end}'

oc -n openshift-gitops get app bpce-kafka-lab \
  -o jsonpath='msg={.status.operationState.message}{"\n"}'

oc -n openshift-gitops get app bpce-events-demo-lab \
  -o jsonpath='msg={.status.operationState.message}{"\n"}'
```
Objectif : valider `Succeeded` + `successfully synced`.

---

## 5) Vérifs Kafka/Strimzi (ports, user, topic, test client)

### 5.1 Ports du bootstrap
```bash
oc -n bpce-platform get svc bpce-kafka-kafka-bootstrap -o jsonpath='{range .spec.ports[*]}{.name}={.port}{"\n"}{end}' | sort
```
Objectif : confirmer ports (clients/replication/scram).

### 5.2 KafkaUser prêt + secret
```bash
oc -n bpce-platform get kafkauser bpce-app -o jsonpath='{range .status.conditions[*]}{.type}={.status} reason={.reason} msg={.message}{"\n"}{end}'
oc -n bpce-platform get secret bpce-app -o jsonpath='name={.metadata.name} hasPassword={.data.password}{"\n"}'
```
Objectif : confirmer `Ready=True` et présence du mot de passe (base64).

### 5.3 Test end-to-end (list/produce/consume)
```bash
PASS_B64="$(oc -n bpce-platform get secret bpce-app -o jsonpath='{.data.password}')"

oc -n bpce-platform exec kafka-client -- bash -lc '
set -euo pipefail
BOOTSTRAP=bpce-kafka-kafka-bootstrap:9093
USER=bpce-app
PASS_B64="'"$PASS_B64"'"
PASS="$(echo "$PASS_B64" | base64 -d)"

cat >/tmp/client.properties <<EOF
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="${USER}" password="${PASS}";
EOF

echo "== LIST TOPICS =="
/opt/kafka/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP" --command-config /tmp/client.properties --list | sort

echo "== PRODUCE 3 msgs =="
printf "t1\nt2\nt3\n" | /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server "$BOOTSTRAP" --producer.config /tmp/client.properties --topic bpce-events >/dev/null

echo "== CONSUME 3 msgs =="
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server "$BOOTSTRAP" --consumer.config /tmp/client.properties --topic bpce-events --from-beginning --max-messages 3
'
```
Objectif : prouver que Kafka fonctionne (auth SCRAM + topic ok).

---

## 6) Vérifs OpenShift (ImageStream/BuildConfig/Deploy/Route)

```bash
oc -n bpce-platform get imagestream,bc,svc,deploy,route -l app=bpce-events-demo -o name || true
oc -n bpce-platform get imagestream bpce-events-demo -o wide || true
oc -n bpce-platform get bc bpce-events-demo -o wide || true
oc -n bpce-platform get deploy bpce-events-demo -o wide || true
oc -n bpce-platform get route bpce-events-demo -o wide || true
```
Objectif : confirmer que les objets existent et vérifier l’état.

### 6.1 Détail Argo sur l’app demo
```bash
oc -n openshift-gitops describe app bpce-events-demo-lab | tail -n 120
```
Objectif : voir quels objets restent `OutOfSync` (souvent `BuildConfig` quand le build “binary” n’a pas été lancé).

---

## 7) Debug ImagePullBackOff (cause: image non présente dans l’ImageStreamTag)

```bash
oc -n bpce-platform get pods -l app=bpce-events-demo -o wide || true

POD="$(oc -n bpce-platform get pod -l app=bpce-events-demo -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || true)"
echo "POD=$POD"
[ -n "$POD" ] && oc -n bpce-platform describe pod "$POD" | tail -n 120 || true

oc -n bpce-platform describe deploy bpce-events-demo | tail -n 80 || true

oc -n bpce-platform get istag bpce-events-demo:latest -o name || true
```
Objectif : comprendre l’échec `ImagePullBackOff`.
Constat vu : le pod essaie de pull `bpce-events-demo:latest` depuis `docker.io/library/...` (accès refusé) parce que l’`ImageStreamTag bpce-events-demo:latest` n’existe pas encore.

---

## 8) Git (état local + contenu du fichier)

```bash
git status
cat apps/quarkus/bpce-events-demo/src/main/resources/application.properties
```
Objectif : vérifier ce qui est modifié / à committer et le contenu réel de la config.

---

## 9) Maven + JAVA_HOME sous Git Bash Windows (JDK17)

### 9.1 Tentative auto (qui a échoué)
```bash
cd /c/workspaces/2026/bpce-natixis-gitops-openshift-quarkus-lab/apps/quarkus/bpce-events-demo

JDK="$(ls -d "/c/Program Files/Eclipse Adoptium/"jdk-17* "/c/Program Files/Java/"jdk-17* 2>/dev/null | head -n1)"
echo "JDK=$JDK"

export JAVA_HOME="$JDK"
export PATH="$JAVA_HOME/bin:$PATH"

java -version
mvn -v
```
Objectif : utiliser JDK 17 depuis Git Bash.

### 9.2 Fix correct (JAVA_HOME Windows + alias mvn.cmd)
```bash
JDK_DIR="$(ls -d /c/Program\ Files/Eclipse\ Adoptium/jdk-17* | sort | tail -n 1)"
export JAVA_HOME="$(cygpath -w "$JDK_DIR")"
export PATH="$JDK_DIR/bin:/c/maven/apache-maven-3.9.8/bin:$PATH"
alias mvn='mvn.cmd'

echo "$JAVA_HOME"
mvn -v
```
Objectif : résoudre l’erreur Maven “JAVA_HOME environment variable is not defined correctly”.

---

## 10) Build Quarkus final (SUCCESS)

```bash
mvn -DskipTests package
ls -la target/quarkus-app/quarkus-run.jar
ls -la target/quarkus-app/
```
Objectif : produire l’artefact `quarkus-run.jar`.

---

## Point d’avancement (ce que montrent les preuves fournies)

- Kafka (Strimzi) : OK (topic visible, produce/consume OK)
- Argo RBAC : OK (tous les `oc auth can-i` en `yes`)
- App demo : ressources créées (IS/BC/Deploy/Route présents)
- Blocage runtime : `ImagePullBackOff` car l’image `bpce-events-demo:latest` n’a pas encore été injectée dans l’ImageStreamTag (`istag NotFound`) → il faut lancer le build “binary” (`oc start-build ... --from-dir

