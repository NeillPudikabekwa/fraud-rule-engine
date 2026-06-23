# Fraud Rule Engine Service

A production-style backend service for processing categorized financial transaction events, applying configurable fraud rules, storing fraud decisions, and exposing the results through REST APIs.

This project was selected from the Capitec backend project briefs: **Fraud Rule Engine Service**.

## Why this project

The fraud rule engine demonstrates backend engineering skills that are important in a banking environment:

- clean REST API design
- validation and error handling
- persistence and query APIs
- rule-based business logic
- configuration-driven thresholds
- test coverage
- Dockerized delivery
- health checks and OpenAPI documentation

## Tech stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 database with file persistence inside Docker volume
- Bean Validation
- Spring Boot Actuator
- OpenAPI / Swagger UI
- JUnit 5 and MockMvc
- Docker multi-stage build

## Architecture

```text
Client / API Consumer
        |
        v
FraudController
        |
        v
FraudEvaluationService
        |
        +--> FraudRule implementations
        |       - HighAmountRule
        |       - HighRiskCountryRule
        |       - VelocityRule
        |
        +--> FraudDecisionRepository
        +--> TransactionEventRepository
        |
        v
H2 Data Store
```

The rule engine uses a simple strategy pattern. Each fraud rule implements `FraudRule`, making it easy to add new rules without changing the controller or API contract.

## Fraud rules implemented

| Rule | Description | Default config |
|---|---|---|
| High Amount | Flags transactions greater than or equal to the configured threshold | `50000` |
| High Risk Country | Flags transactions from configured high-risk countries | `NG,RU,KP,IR` |
| Velocity | Flags customers exceeding the transaction count within a time window | `3 transactions / 10 minutes` |

Risk levels are resolved from the number of triggered rules:

| Triggered rules | Risk level |
|---:|---|
| 0 | LOW |
| 1 | MEDIUM |
| 2 | HIGH |
| 3+ | CRITICAL |

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/transactions/evaluate` | Process one transaction and return fraud decision |
| GET | `/api/v1/fraud-decisions` | List decisions with optional filters |
| GET | `/api/v1/fraud-decisions/{id}` | Retrieve one fraud decision |
| GET | `/api/v1/fraud-decisions/summary` | Retrieve fraud metrics summary |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | Swagger UI |

## Run with Docker

```bash
docker build -t fraud-rule-engine .
docker run --rm -p 8080:8080 fraud-rule-engine
```

Or with Docker Compose:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Run locally

Requirements:

- Java 21
- Maven 3.9+

```bash
mvn clean test
mvn spring-boot:run
```

## Run tests

```bash
mvn test
```

## Sample request

```bash
curl -X POST http://localhost:8080/api/v1/transactions/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "transactionReference": "TXN-20260617-0001",
    "customerId": "CUST-1001",
    "amount": 75000.00,
    "currency": "ZAR",
    "category": "TRANSFER",
    "merchant": "Online Banking",
    "countryCode": "ZA",
    "channel": "MOBILE_APP",
    "transactionTime": "2026-06-17T08:30:00Z"
  }'
```

Example response:

```json
{
  "decisionId": "generated-uuid",
  "transactionReference": "TXN-20260617-0001",
  "customerId": "CUST-1001",
  "amount": 75000.00,
  "currency": "ZAR",
  "category": "TRANSFER",
  "merchant": "Online Banking",
  "countryCode": "ZA",
  "channel": "MOBILE_APP",
  "flagged": true,
  "riskLevel": "MEDIUM",
  "reasons": [
    "HIGH_AMOUNT: transaction amount is greater than or equal to configured threshold"
  ],
  "transactionTime": "2026-06-17T08:30:00Z",
  "evaluatedAt": "generated-timestamp"
}
```

## Filter examples

List flagged transactions:

```bash
curl "http://localhost:8080/api/v1/fraud-decisions?flagged=true&page=0&size=20"
```

List decisions for one customer:

```bash
curl "http://localhost:8080/api/v1/fraud-decisions?customerId=CUST-1001"
```

View summary metrics:

```bash
curl http://localhost:8080/api/v1/fraud-decisions/summary
```

## Production considerations

For a real banking production deployment, I would extend this solution with:

- PostgreSQL instead of H2
- Kafka or another durable event stream for transaction ingestion
- authentication and authorization on the APIs
- audit logs for every rule decision
- rule versioning so historical decisions remain explainable
- idempotency keys for transaction ingestion
- observability with distributed tracing and structured logs
- CI/CD pipeline with static analysis, dependency scanning, and container scanning
- encrypted secrets using a proper secret manager

## Project structure

```text
src/main/java/com/capitec/fraud
├── api              REST controllers
├── config           configuration properties
├── domain           JPA entities and enums
├── dto              request/response models
├── exception        API exception handling
├── repository       data access
├── rule             fraud rule strategy implementations
└── service          business orchestration
```
