.PHONY: help start build start-prod stop-prod restart-prod logs-prod logs-all-prod build-prod clean-prod status-prod db-connect db-shell db-shell-dev test start-dev stop-dev logs-dev build-dev migrate info-migrate repair-migrate clean-migrate migrate-dev info-migrate-dev repair-migrate-dev clean-migrate-dev

# Default target
help:
	@echo "Final Store - Available Commands"
	@echo "================================="
	@echo ""
	@echo "Local development:"
	@echo "  make start         - Start the application locally"
	@echo "  make build         - Build the application locally"
	@echo ""
	@echo "Production Mode:"
	@echo "  make start-prod    - Start the application (production mode)"
	@echo "  make stop-prod     - Stop the application (production mode)"
	@echo "  make restart-prod  - Restart the application (production mode)"
	@echo "  make logs-prod     - View application logs (production mode)"
	@echo "  make logs-all-prod - View all service logs (production mode)"
	@echo "  make build-prod    - Rebuild and start the application (production mode)"
	@echo "  make clean-prod    - Stop and remove all containers and volumes (production mode)"
	@echo ""
	@echo "Development Mode (Hot Reload):"
	@echo "  make start-dev     - Start in development mode with hot reload"
	@echo "  make stop-dev      - Stop development environment"
	@echo "  make logs-dev      - View development logs"
	@echo "  make build-dev     - Rebuild and start development environment"
	@echo ""
	@echo "Database:"
	@echo "  make db-connect    - Connect to MySQL database from host (production mode)"
	@echo "  make db-shell      - Open MySQL shell in container (production mode)"
	@echo "  make db-shell-dev  - Open MySQL shell in container (development mode)"
	@echo ""
	@echo "Migrations (Local - port 3306):"
	@echo "  make migrate       - Run Flyway migrations"
	@echo "  make info-migrate  - Show migration status"
	@echo "  make repair-migrate - Repair migration metadata"
	@echo "  make clean-migrate - Clean database (WARNING: deletes all data)"
	@echo ""
	@echo "Migrations (Development - port 3307):"
	@echo "  make migrate-dev   - Run Flyway migrations on development database"
	@echo "  make info-migrate-dev - Show migration status on development database"
	@echo "  make repair-migrate-dev - Repair migration metadata on development database"
	@echo "  make clean-migrate-dev - Clean development database (WARNING: deletes all data)"
	@echo ""
	@echo "Other:"
	@echo "  make status-prod   - Show status of all services (production mode)"
	@echo ""

start:
	@echo "Starting Final Store..."
	@./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

build:
	@echo "Building Final Store..."
	@./mvnw clean install

# Start the application in production mode
start-prod:
	@echo "Starting Final Store (production mode)..."
	@./start.sh

# Stop the application in production mode
stop-prod:
	@echo "Stopping Final Store (production mode)..."
	@docker-compose down

# Restart the application in production mode
restart-prod:
	@echo "Restarting Final Store (production mode)..."
	@docker-compose restart

# View application logs in production mode
logs-prod:
	@docker-compose logs -f app

# View all logs in production mode
logs-all-prod:
	@docker-compose logs -f

# Rebuild and start in production mode
build-prod:
	@echo "Rebuilding and starting Final Store (production mode)..."
	@docker-compose up -d --build

# Clean everything in production mode
clean-prod:
	@echo "Cleaning up Final Store (production mode - this will remove all data)..."
	@docker-compose down -v
	@echo "Cleanup complete!"

# Connect to MySQL from host
db-connect:
	@mysql -h 127.0.0.1 -P 3307 -u root -p0000 my_store

# Open MySQL shell in container
db-shell:
	@docker exec -it final-store-mysql mysql -uroot -p0000 my_store

db-shell-dev:
	@docker exec -it final-store-mysql-dev mysql -uroot -p0000 my_store

# Show status in production mode
status-prod:
	@docker-compose ps

# Development mode commands
start-dev:
	@echo "Starting in development mode with hot reload..."
	@./start-dev.sh

stop-dev:
	@echo "Stopping development environment..."
	@docker-compose -f docker-compose.dev.yml down

logs-dev:
	@docker-compose -f docker-compose.dev.yml logs -f app

build-dev:
	@echo "Rebuilding and starting development environment..."
	@docker-compose -f docker-compose.dev.yml up -d --build

# Migration commands (local database on port 3306)
migrate:
	@echo "Running Flyway migrations..."
	@./mvnw flyway:migrate

info-migrate:
	@echo "Checking migration status..."
	@./mvnw flyway:info

repair-migrate:
	@echo "Repairing Flyway migration metadata..."
	@./mvnw flyway:repair

clean-migrate:
	@echo "WARNING: This will delete all data in the database!"
	@read -p "Are you sure? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		./mvnw flyway:clean; \
	else \
		echo "Operation cancelled."; \
	fi

# Migration commands for development Docker database (port 3307)
migrate-dev:
	@echo "Running Flyway migrations on development database..."
	@./mvnw flyway:migrate -Dflyway.url='jdbc:mysql://localhost:3307/my_store?useSSL=false&allowPublicKeyRetrieval=true' -Dflyway.user=root -Dflyway.password=0000

info-migrate-dev:
	@echo "Checking migration status on development database..."
	@./mvnw flyway:info -Dflyway.url='jdbc:mysql://localhost:3307/my_store?useSSL=false&allowPublicKeyRetrieval=true' -Dflyway.user=root -Dflyway.password=0000

repair-migrate-dev:
	@echo "Repairing Flyway migration metadata on development database..."
	@./mvnw flyway:repair -Dflyway.url='jdbc:mysql://localhost:3307/my_store?useSSL=false&allowPublicKeyRetrieval=true' -Dflyway.user=root -Dflyway.password=0000

clean-migrate-dev:
	@echo "WARNING: This will delete all data in the development database!"
	@read -p "Are you sure? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		./mvnw flyway:clean -Dflyway.url='jdbc:mysql://localhost:3307/my_store?useSSL=false&allowPublicKeyRetrieval=true' -Dflyway.user=root -Dflyway.password=0000; \
	else \
		echo "Operation cancelled."; \
	fi
