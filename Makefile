# Variables
IMAGE_NAME := healthatlas-auth
DOCKERFILE := Dockerfile.jvm
FALLBACK_DOCKERFILE := Dockerfile.jvm.fallback

# Default target
restart: docker-down build docker-build docker-up

# Stop running containers
docker-down:
	@echo "🛑 Stopping Docker Compose..."
	docker-compose down || true

# Build project
build:
	@echo "📦 Building the project (skipping tests)..."
	./gradlew build -x test

# Copy built jar
copy-jar:
	@echo "📄 Copying JAR..."
	cp build/libs/*.jar healthatlas-auth.jar

# Docker build with fallback logic
docker-build: copy-jar
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
