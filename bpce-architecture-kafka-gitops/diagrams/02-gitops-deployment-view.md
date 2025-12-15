# 02 — GitOps & Deployment View

```mermaid
flowchart TB
  subgraph openshift-gitops
    ARGO[Argo CD (OpenShift GitOps)]
    PROJ[AppProject bpce]
    ROOT[Application: bpce-root (App-of-Apps)]
  end

  subgraph Git Repository
    REPO[(bpce-natixis-gitops-openshift-quarkus-lab)]
    PATH1[gitops/apps/*]
    PATH2[kafka/*]
    PATH3[apps/*]
  end

  subgraph openshift-operators
    SUB[Subscription: AMQ Streams / Strimzi]
    CSV[ClusterServiceVersion]
    OP[Operator Pod(s)]
  end

  subgraph bpce-platform
    KC[Kafka CR]
    KP[Kafka Pods]
    KSVC[Bootstrap Service]
    KT[KafkaTopic bpce.events]
  end

  subgraph bpce-dev
    APP[Deployment backend]
    SVCB[Service backend]
    ROUTE[Route backend]
  end

  REPO -->|sync| ARGO
  ARGO --> PROJ
  ARGO --> ROOT
  ROOT --> SUB
  ROOT --> KC
  ROOT --> KT
  ROOT --> APP
  SUB --> CSV --> OP
  OP --> KC --> KP
  KC --> KSVC
  KP --> KT
  SVCB --> APP --> ROUTE
```
