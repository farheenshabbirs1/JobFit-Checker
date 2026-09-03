# Terraform (reference only)

This provisions the Azure infrastructure the resume bullet describes -- an Azure Kubernetes
Service cluster and a container registry for the four images -- so the shape of a real
deployment is on record. **It has not been run or applied in this environment**: doing so
needs an actual Azure subscription and credentials this project doesn't have, so treat it as a
documented starting point, not verified-working infrastructure. Run `terraform plan` yourself
against your own subscription before ever running `apply`.

## What's here

- `main.tf` -- resource group, AKS cluster, Azure Container Registry, and the role
  assignment that lets AKS pull from that registry.
- `variables.tf` -- the inputs (location, cluster name, node count/size, etc).
- `outputs.tf` -- the values you'd need afterwards (AKS credentials command, ACR login server).

## What's not here

- The blue-green deployment mechanics themselves. That's a Kubernetes-level concern (two
  Deployments -- e.g. `api-service-blue` / `api-service-green` -- fronted by a Service whose
  selector is flipped between them, or two ingress backends with weighted traffic), not
  something Terraform typically owns. `infra/k8s/` has plain manifests for the app; wiring
  those into a blue-green rollout (Argo Rollouts, Flagger, or a hand-rolled two-Deployment
  swap) is a deliberately separate, later step.
- Postgres as a managed Azure service (Azure Database for PostgreSQL). The manifests here run
  Postgres in-cluster via the same image docker-compose uses, which is fine for a demo/portfolio
  deployment but not what you'd run in production -- swapping in a managed instance means
  pointing `JOBFIT_DB_URL` at it and dropping the in-cluster Postgres manifest.
- Secrets management. `JOBFIT_DB_PASSWORD` and friends are plain Kubernetes Secrets here, not
  Azure Key Vault-backed -- fine for reference, not for a real deployment.
