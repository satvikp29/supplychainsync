# SupplyChainSync

Enterprise-grade Java microservices project using **Spring Boot**, **Kafka**, **Kubernetes**, **AWS**, **Terraform**, **GitHub Actions CI/CD**, observability (**Prometheus** / **Grafana**), and **JWT** auth.

## Architecture

- **shipment-service** (port 8082): Publishes shipment events to Kafka; exposes JWT token endpoint and protected API.
- **inventory-service** (port 8081): Consumes shipment events from Kafka, persists to PostgreSQL; exposes protected REST API.
- **Kafka**: Event streaming between services.
- **PostgreSQL**: Persistence for inventory/shipment events.
- **Prometheus** + **Grafana**: Metrics and dashboards.

## Prerequisites

- Java 21, Maven 3.9+
- Docker & Docker Compose (for local stack)
- (Optional) kubectl, Terraform, AWS CLI for K8s and AWS

## Quick start (local)

1. **Start infrastructure and services:**

   ```bash
   cd infra/local
   docker compose -f docker-compose.yml -f docker-compose.services.yml up -d
   ```

2. **Get a JWT and call APIs:**

   ```bash
   # Get token (no auth required for /auth/token)
   TOKEN=$(curl -s -X POST http://localhost:8082/auth/token \
     -H "Content-Type: application/json" \
     -d '{"username":"demo"}' | jq -r '.access_token')

   # Call protected shipment endpoint
   curl -H "Authorization: Bearer $TOKEN" "http://localhost:8082/publish?msg=Order123"

   # Call protected inventory events endpoint
   curl -H "Authorization: Bearer $TOKEN" "http://localhost:8081/events"
   ```

3. **Health and metrics (no JWT):**

   - Shipment: http://localhost:8082/health, http://localhost:8082/actuator/prometheus  
   - Inventory: http://localhost:8081/health, http://localhost:8081/actuator/prometheus  

4. **Observability:**

   - Prometheus: http://localhost:9090  
   - Grafana: http://localhost:3000 (admin / admin); add Prometheus data source `http://prometheus:9090`.

## JWT auth

- **Issue token:** `POST /auth/token` on shipment-service (body optional: `{"username":"..."}`). Returns `{"access_token":"...", "token_type":"Bearer"}`.
- **Use token:** `Authorization: Bearer <token>` on protected endpoints (`/publish`, `/events`).
- **Secret:** Set `JWT_SECRET` (min 256 bits for HS256) in production; default is a dev-only value in config.

## Running services only (no Docker)

1. Start Kafka and Postgres (e.g. `docker compose -f docker-compose.yml up -d` in `infra/local`).
2. Run from repo root:

   ```bash
   cd services/inventory-service && mvn spring-boot:run
   cd services/shipment-service && mvn spring-boot:run
   ```

## Kubernetes

1. Build and load images (or use your registry):

   ```bash
   cd services/inventory-service && mvn package && docker build -t inventory-service:latest .
   cd services/shipment-service && mvn package && docker build -t shipment-service:latest .
   ```

2. Create namespace and secrets (override default secret in prod):

   ```bash
   kubectl apply -f infra/kubernetes/namespace.yaml
   kubectl apply -f infra/kubernetes/secret.yaml
   kubectl apply -f infra/kubernetes/configmap.yaml
   ```

3. Deploy (ensure Kafka and Postgres are available in-cluster or via external endpoints; update ConfigMap/Secret as needed):

   ```bash
   kubectl apply -f infra/kubernetes/inventory-service.yaml
   kubectl apply -f infra/kubernetes/shipment-service.yaml
   kubectl apply -f infra/kubernetes/prometheus.yaml
   kubectl apply -f infra/kubernetes/grafana.yaml
   ```

## AWS (Terraform)

- **ECR:** Terraform in `infra/terraform` creates ECR repositories for both services. See `infra/terraform/README.md`.
- **Usage:** `terraform init && terraform apply` (after configuring AWS credentials and optional `terraform.tfvars`).

## CI/CD (GitHub Actions)

- **CI** (`.github/workflows/ci.yml`): On push/PR to `main` or `develop`, builds and runs tests for both services (Maven, JDK 21).
- **CD** (`.github/workflows/cd.yml`): On push to `main`, builds JARs, builds Docker images, and pushes to Amazon ECR.
  - **Secrets:** `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` (and ensure ECR repos exist, e.g. via Terraform).
  - Optional: use OIDC with `AWS_ROLE_ARN` instead of access keys.

## Project layout

```
├── .github/workflows/     # CI and CD
├── infra/
│   ├── local/             # Docker Compose (Kafka, Postgres, apps, Prometheus, Grafana)
│   ├── kubernetes/        # K8s manifests (namespace, configmap, secret, deployments, services)
│   └── terraform/         # AWS ECR and variables
├── services/
│   ├── inventory-service/ # Spring Boot, Kafka consumer, JPA, JWT, Prometheus
│   └── shipment-service/  # Spring Boot, Kafka producer, JWT issuer, Prometheus
└── README.md
```

## License

Use as needed for your organization.
