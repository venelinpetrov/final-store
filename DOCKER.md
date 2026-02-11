# Docker Setup Guide

This document provides detailed information about running the Final Store application using Docker.

## Architecture

The Docker setup consists of two services:

1. **MySQL Database** (`mysql` service)
   - Image: `mysql:8.0`
   - Port: `3307` (host) → `3306` (container) - Uses 3307 to avoid conflict with local MySQL
   - Database: `my_store`
   - Persistent storage via Docker volume

2. **Spring Boot Application** (`app` service)
   - Built from source using multi-stage Dockerfile
   - Port: `8080`
   - Depends on MySQL service
   - Runs Flyway migrations automatically on startup

## Quick Start

```bash
./start.sh
```

## Configuration

### Environment Variables

The application uses environment variables defined in the `.env` file:

- `JWT_SECRET`: Secret key for JWT token generation (required)
- `MYSQL_ROOT_PASSWORD`: MySQL root password (default: `0000`)
- `MYSQL_DATABASE`: Database name (default: `my_store`)
- `MYSQL_PORT`: MySQL port on host machine (default: `3307`)

### Application Profiles

The Docker setup uses the `docker` Spring profile, which is configured in `src/main/resources/application-docker.properties`.

## Docker Commands

### Start the application
```bash
docker-compose up -d
```

### View logs
```bash
# All services
docker-compose logs -f

# Application only
docker-compose logs -f app

# MySQL only
docker-compose logs -f mysql
```

### Stop the application
```bash
docker-compose down
```

### Stop and remove all data (including database)
```bash
docker-compose down -v
```

### Rebuild the application
```bash
docker-compose up -d --build
```

### Restart a specific service
```bash
docker-compose restart app
```

## Database Access

### Connect to MySQL from host machine
```bash
mysql -h 127.0.0.1 -P 3307 -u root -p0000 my_store
```

### Connect to MySQL container
```bash
docker exec -it final-store-mysql mysql -uroot -p0000 my_store
```

### Run SQL scripts
```bash
docker exec -i final-store-mysql mysql -uroot -p0000 my_store < script.sql
```

## Troubleshooting

### Application won't start

1. Check if MySQL is healthy:
   ```bash
   docker-compose ps
   ```

2. View application logs:
   ```bash
   docker-compose logs app
   ```

3. Ensure JWT_SECRET is set in `.env` file

### Database connection issues

1. Verify MySQL is running:
   ```bash
   docker-compose ps mysql
   ```

2. Check MySQL logs:
   ```bash
   docker-compose logs mysql
   ```

3. Ensure the database is created:
   ```bash
   docker exec -it final-store-mysql mysql -uroot -p0000 -e "SHOW DATABASES;"
   ```

### Port conflicts

The MySQL service is configured to use port 3307 by default to avoid conflicts with local MySQL installations.

If you need to change ports, you can modify the `.env` file:

```bash
# Change MySQL port
MYSQL_PORT=3308

# For the application port, modify docker-compose.yml:
# services:
#   app:
#     ports:
#       - "8081:8080"  # Change host port to 8081
```

### Clean slate restart

To completely reset the application and database:

```bash
docker-compose down -v
docker-compose up -d --build
```

## Development Workflow

### Making code changes

1. Make your code changes
2. Rebuild and restart:
   ```bash
   docker-compose up -d --build app
   ```

### Running migrations

Flyway migrations run **automatically** when the application starts. You don't need to run migrations manually.

#### Troubleshooting Failed Migrations

If the app crashes on startup with "Flyway validation failed", a migration failed mid-execution:

**For Development Mode (`make dev`):**
```bash
# Stop the environment
make dev-stop

# Start only MySQL
docker-compose -f docker-compose.dev.yml up -d mysql

# Repair Flyway schema
make migrate-repair-dev

# Restart everything
make dev
```

**For Production Mode (`make start`):**
```bash
# Stop the environment
make stop

# Start only MySQL
docker-compose up -d mysql

# Repair Flyway schema (connect to port 3307)
./mvnw flyway:repair -Dflyway.url='jdbc:mysql://localhost:3307/my_store?useSSL=false&allowPublicKeyRetrieval=true' -Dflyway.user=root -Dflyway.password=0000

# Restart everything
make start
```

### Accessing the application

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs

## Production Considerations

For production deployment, consider:

1. **Use secrets management** instead of `.env` file
2. **Use external database** instead of containerized MySQL
3. **Add health checks** and monitoring
4. **Configure resource limits** in docker-compose.yml
5. **Use production-grade JWT secret** (longer and more complex)
6. **Enable SSL/TLS** for database connections
7. **Set up proper logging** and log aggregation
8. **Use Docker secrets** for sensitive data
9. **Implement backup strategy** for database
10. **Use container orchestration** (Kubernetes, Docker Swarm)

## File Structure

```
.
├── Dockerfile                              # Multi-stage build for the application
├── docker-compose.yml                      # Docker Compose configuration
├── .dockerignore                           # Files to exclude from Docker build
├── .env                                    # Environment variables (not in git)
├── .env.example                            # Example environment variables
├── start.sh                                # Quick start script
└── src/main/resources/
    └── application-docker.properties       # Docker-specific Spring configuration
```

