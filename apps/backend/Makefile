APP=container-tools-api
IMG ?= quay.io/CHANGE_ME/$(APP):dev

.PHONY: test build image

test:
	./mvnw -q test

build:
	./mvnw -q package -DskipTests

image: build
	docker build -f src/main/docker/Dockerfile.jvm -t $(IMG) .
