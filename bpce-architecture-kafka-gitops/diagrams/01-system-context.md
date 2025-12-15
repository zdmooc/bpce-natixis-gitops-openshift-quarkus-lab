# 01 — System Context

```mermaid
flowchart LR
  U[Utilisateur / API Client] -->|HTTP| R[OpenShift Route]
  R --> SVC[Service backend]
  SVC --> POD[Pod Quarkus backend]

  subgraph OpenShift Cluster (CRC / OCP)
    subgraph Namespace bpce-dev
      SVC
      POD
      OTEL[otel-collector]
    end

    subgraph Namespace bpce-platform
      KAFKA[Kafka Cluster (Strimzi/AMQ Streams)]
      TOPIC[(Topic: bpce.events)]
    end

    KAFKA --> TOPIC
    POD -->|produce/consume| KAFKA
    POD -->|OTLP| OTEL
  end
```
