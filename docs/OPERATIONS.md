# Opérations (SRE-lite)

## SLIs/SLOs (exemples)
- disponibilité API : 99.9%
- latence p95 : < 300ms
- taux d’erreur 5xx : < 0.1%

## Observabilité
- Health : /q/health
- Metrics : /q/metrics
- Traces : OTLP -> collector -> Jaeger

## Troubleshooting checklist
- [ ] route -> service -> endpoints
- [ ] readinessProbe OK
- [ ] resources (OOMKilled? throttling CPU?)
- [ ] logs JSON + corrélation traceId (OTel)
