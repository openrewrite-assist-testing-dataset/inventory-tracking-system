# Inventory Tracking System

A multi-module inventory tracking system built with Dropwizard 2.1.x, MySQL, and JWT authentication.

## Features

- Multi-module architecture (api, core, db modules)
- JWT-based authentication
- MySQL database with Liquibase migrations
- Mix of JDBI and raw JDBC operations
- RESTful API with Swagger 2.0 documentation
- Comprehensive inventory management

## Prerequisites

- Java 14
- MySQL 8.0+
- Gradle 7.5+

## Modules

- **api**: REST API endpoints and authentication
- **core**: Domain models and business logic
- **db**: Database access layer with JDBI and raw JDBC

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd inventory-tracking-system
   ```

2. Start MySQL database:
   ```bash
   docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=inventory_db -e MYSQL_USER=inventory -e MYSQL_PASSWORD=inventory123 mysql:8.0
   ```

3. Build the application:
   ```bash
   ./gradlew build
   ```

4. Run database migrations:
   ```bash
   ./gradlew run --args="db migrate src/main/resources/config.yml"
   ```

5. Start the application:
   ```bash
   ./gradlew run --args="server src/main/resources/config.yml"
   ```

## API Endpoints

- `GET /api/v1/items` - List all items
- `GET /api/v1/items/{id}` - Get item by ID
- `GET /api/v1/items/category/{category}` - Get items by category
- `GET /api/v1/items/low-stock` - Get low stock items
- `GET /api/v1/items/expensive` - Get expensive items
- `POST /api/v1/items` - Create new item
- `PUT /api/v1/items/{id}` - Update item
- `PUT /api/v1/items/{id}/quantity` - Adjust item quantity

## Authentication

The service uses JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Documentation

Swagger documentation is available at: `http://localhost:8080/swagger.json`

## Docker

Build and run with Docker:

```bash
./gradlew build
docker build -t inventory-tracking-system .
docker run -p 8080:8080 inventory-tracking-system
```

## Kubernetes Deployment

Deploy using Helm:

```bash
helm install inventory-tracking-system helm/inventory-tracking-system
```