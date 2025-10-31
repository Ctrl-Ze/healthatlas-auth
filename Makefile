# Makefile - minimal pragmatic targets for local dev and docker

# Config
APP_NAME := healthatlas-auth
IMAGE_NAME := healthatlas-auth
DOCKER_COMPOSE := docker-compose
PROJECT_ROOT := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

# Port mapping for local DB container
LOCAL_DB_HOST_PORT := 5434
LOCAL_DB_CONTAINER := cerberus-local-db
LOCAL_DB_VOLUME := cerberus-local-pgdata

# Default: list useful targets
.DEFAULT_GOAL := help

help:
	@echo "Common targets:"
	@echo "  make db-local-up        Start a local Postgres for dev (host port $(LOCAL_DB_HOST_PORT))"
	@echo "  make db-local-down      Stop & remove local Postgres container"
	@echo "  make dev-local          Start Quarkus in dev mode (profile=dev)"
	@echo "  make build              Build jar / quarkus-app locally"
	@echo "  make docker-up          Build app and bring up docker-compose (profile=docker)"
	@echo "  make docker-down        docker-compose down"
	@echo "  make docker-redeploy    Quick rebuild image and restart service (docker compose)"
	@echo "  make logs               Tail service logs"
	@echo "  make db                 Connect to docker postgres psql"

# -------------------------
# Local DB on host (dev)
# -------------------------
db-local-up:
	@echo "🟢 Starting local Postgres for dev (name=$(LOCAL_DB_CONTAINER) -> host: localhost:$(LOCAL_DB_HOST_PORT))"
	docker rm -f $(LOCAL_DB_CONTAINER) >/dev/null 2>&1 || true
	docker run --name $(LOCAL_DB_CONTAINER) \
		-e POSTGRES_USER=postgres \
		-e POSTGRES_PASSWORD=postgres \
		-e POSTGRES_DB=cerberus \
		-p $(LOCAL_DB_HOST_PORT):5432 \
		-v $(LOCAL_DB_VOLUME):/var/lib/postgresql/data \
		-d postgres:15

db-local-down:
	@echo "🛑 Stopping local Postgres..."
	docker rm -f $(LOCAL_DB_CONTAINER) >/dev/null 2>&1 || true
	docker volume rm $(LOCAL_DB_VOLUME) >/dev/null 2>&1 || true

# -------------------------
# Run Quarkus locally (dev)
# -------------------------
dev-local:
	@echo "🔧 Starting Quarkus dev (profile=dev) — ensure DB on localhost:$(LOCAL_DB_HOST_PORT)"
	@echo "👉 Tip: open another terminal and attach a remote debugger to port 5005"
	cd $(PROJECT_ROOT) && ./gradlew quarkusDev -Dquarkus.profile=dev

# -------------------------
# Build (locally)
# -------------------------
build:
	@echo "📦 Building the project (clean + quarkusBuild) - skipping tests..."
	cd $(PROJECT_ROOT) && ./gradlew clean quarkusBuild -x test

# -------------------------
# Docker (compose) helpers
# -------------------------
docker-up:
	@echo "🚀 Building app locally then docker-compose up (profile=docker)"
	cd $(PROJECT_ROOT) && ./gradlew clean quarkusBuild -x test
	cd $(PROJECT_ROOT) && $(DOCKER_COMPOSE) up --build -d

docker-down:
	@echo "🛑 Stopping stack..."
	cd $(PROJECT_ROOT) && $(DOCKER_COMPOSE) down

docker-redeploy:
	@echo "🔁 Rebuilding Quarkus app and restarting container..."
	cd $(PROJECT_ROOT) && ./gradlew clean quarkusBuild -x test
	cd $(PROJECT_ROOT) && $(DOCKER_COMPOSE) up --build -d --force-recreate --no-deps $(APP_NAME)

# copy-jar (if you still have Dockerfile that uses jar rather than quarkus-app)
copy-jar:
	@echo "📄 Copying JAR..."
	cp build/libs/*.jar $(IMAGE_NAME).jar

# -------------------------
# Logs and DB access
# -------------------------
logs:
	@echo "🪵 Tailing logs for $(APP_NAME)..."
	cd $(PROJECT_ROOT) && $(DOCKER_COMPOSE) logs -f $(APP_NAME)

db:
	@echo "🗄️ Connecting to Cerberus DB in docker..."
	docker exec -it cerberus-db psql -U postgres -d cerberus

otel-up:
	docker-compose -f otel-compose.yml up -d

otel-down:
	docker-compose -f otel-compose.yml down


# -------------------------
# Clean convenience
# -------------------------
clean:
	cd $(PROJECT_ROOT) && ./gradlew clean