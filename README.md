# SupplyChainSync

Enterprise Java microservices: **Spring Boot**, **Kafka**, **Kubernetes**, **AWS**, **Terraform**, **GitHub Actions**, **Prometheus/Grafana**, **JWT auth**.

---

## Try it (5 minutes)

**Prerequisites:** Docker, Java 21, Maven

### 1. Run tests (optional)

```bash
cd services/inventory-service && mvn test
cd ../shipment-service && mvn test
```

You should see `BUILD SUCCESS` for both.

### 2. Start everything

```bash
cd infra/local
docker compose -f docker-compose.yml -f docker-compose.services.yml up -d
```

You should see 7 containers start. **Wait ~45 seconds** for the apps to be ready.

### 3. Get a token

```bash
curl -s -X POST http://localhost:8082/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"demo"}'
```

**Expected:** JSON like `{"access_token":"eyJ...","token_type":"Bearer"}`. Copy the `access_token` value (the long string).

> **404?** Rebuild the image: `cd ../../services/shipment-service && mvn package -DskipTests`, then from `infra/local` run `docker compose -f docker-compose.yml -f docker-compose.services.yml build shipment-service --no-cache && docker compose -f docker-compose.yml -f docker-compose.services.yml up -d shipment-service`

### 4. Publish an event (replace `YOUR_TOKEN` with your token)

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" "http://localhost:8082/publish?msg=Order123"
```

**Expected:** `Published: Order123`

### 5. Read events from inventory

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" "http://localhost:8081/events"
```

**Expected:** JSON array like `[{"id":1,"message":"Order123","receivedAt":"..."}]`

### 6. Check observability (optional)

- **Prometheus:** http://localhost:9090 → Status → Targets (both services should be UP)
- **Grafana:** http://localhost:3000 (login: `admin` / `admin`) → Add data source → Prometheus → URL: `http://prometheus:9090` → Save & Test

---

## What this project does

1. **shipment-service** (port 8082) – publishes shipment events to Kafka, issues JWT tokens
2. **inventory-service** (port 8081) – listens to Kafka, stores events in PostgreSQL
3. When you publish via shipment-service → it goes to Kafka → inventory-service saves it → you can query it

**Flow:** Publish event → Kafka → Inventory stores it → Query events

---

## Endpoints

| Endpoint | Auth? | Description |
|----------|-------|-------------|
| `POST /auth/token` | No | Get JWT (body: `{"username":"demo"}`) |
| `GET /publish?msg=X` | Yes | Publish event to Kafka |
| `GET /events` | Yes | List stored events |
| `GET /health` | No | Health check |
| `GET /actuator/prometheus` | No | Metrics for Prometheus |

---

## Stop everything

```bash
cd infra/local
docker compose -f docker-compose.yml -f docker-compose.services.yml down
```

---

## More options

### Run without Docker (Kafka + Postgres still in Docker)

```bash
cd infra/local && docker compose -f docker-compose.yml up -d
cd ../../services/inventory-service && mvn spring-boot:run   # terminal 1
cd services/shipment-service && mvn spring-boot:run          # terminal 2
```

### Kubernetes

```bash
kubectl apply -f infra/kubernetes/namespace.yaml
kubectl apply -f infra/kubernetes/secret.yaml
kubectl apply -f infra/kubernetes/configmap.yaml
kubectl apply -f infra/kubernetes/inventory-service.yaml
kubectl apply -f infra/kubernetes/shipment-service.yaml
kubectl apply -f infra/kubernetes/prometheus.yaml
kubectl apply -f infra/kubernetes/grafana.yaml
```

*(Requires Kafka and Postgres in-cluster or via ConfigMap)*

### AWS (Terraform)

```bash
cd infra/terraform && terraform init && terraform apply
```

Creates ECR repositories for both services.

### CI/CD

- **CI:** Push to `main`/`develop` → GitHub Actions builds and tests both services
- **CD:** Push to `main` → Builds Docker images and pushes to ECR (needs `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` secrets)

---

## Project layout

```
├── .github/workflows/   # CI and CD
├── infra/local/        # Docker Compose (Kafka, Postgres, apps, Prometheus, Grafana)
├── infra/kubernetes/   # K8s manifests
├── infra/terraform/    # AWS ECR
├── services/
│   ├── inventory-service/
│   └── shipment-service/
└── README.md
```
