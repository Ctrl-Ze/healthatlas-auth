# Makefile for healthatlas-auth

# Variables
IMAGE_NAME := healthatlas-auth
DOCKERFILE := Dockerfile.jvm
FALLBACK_DOCKERFILE := Dockerfile.jvm.fallback

# Default target
restart: docker-down build docker-build docker-up

# Stop running containers
docker-down:
	@echo "🛑 Stopping Docker Compose..."
	docker-compose down -v || true

# Build Quarkus app
build:
	@echo "📦 Building Quarkus app (fast-jar)..."
	./gradlew clean quarkusBuild -x test

# Docker build with fallback logic
docker-build:
	@echo "🐳 Building Docker image..."
	@if docker build -f $(DOCKERFILE) -t $(IMAGE_NAME):latest .; then \
		echo "✅ Docker image built successfully from $(DOCKERFILE)"; \
	else \
		echo "⚠️ Primary build failed — falling back to alternate base image..."; \
		cp $(DOCKERFILE) $(FALLBACK_DOCKERFILE); \
		sed -i.bak 's|FROM eclipse-temurin:21-jre|FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu|' $(FALLBACK_DOCKERFILE); \
		docker build -f $(FALLBACK_DOCKERFILE) -t $(IMAGE_NAME):latest .; \
		rm -f $(FALLBACK_DOCKERFILE) $(FALLBACK_DOCKERFILE).bak; \
	fi

# Start containers
docker-up:
	@echo "🚀 Starting Docker Compose..."
	docker-compose up -d

# Tail logs
logs:
	@echo "🪵 Showing logs for Cerberus..."
	docker-compose logs -f healthatlas-auth

# Access Cerberus DB
db:
	@echo "🗄️ Connecting to Cerberus database..."
	docker exec -it cerberus-db psql -U postgres -d cerberus
