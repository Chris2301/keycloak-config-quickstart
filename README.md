# keycloak-config-quickstart

This project uses Quarkus for its configuration app and a dockerfile which is able to create a running Keycloak 23.0.1 instance

## How to run this application

### Prerequisite

Installed Java 17, Maven (or maven wrapper), Docker, Docker Compose.

### Running 

1. mvn clean package
2. docker compose build
3. docker compose up

Visit http://localhost:8080/admin/master/console/ and login with username:password admin:admin
