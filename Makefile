.PHONY: help start stop restart logs build clean db-connect db-shell test dev dev-stop dev-logs dev-build

# Default target
help:
	@echo "Final Store - Available Commands"
	@echo "================================="
	@echo ""
	@echo "Production Mode:"
	@echo "  make start         - Start the application (production mode)"
	@echo "  make stop          - Stop the application"
	@echo "  make restart       - Restart the application"
	@echo "  make logs          - View application logs"
	@echo "  make logs-all      - View all service logs"
	@echo "  make build         - Rebuild and start the application"
	@echo "  make clean         - Stop and remove all containers and volumes"
	@echo ""
	@echo "Development Mode (Hot Reload):"
	@echo "  make dev           - Start in development mode with hot reload"
	@echo "  make dev-stop      - Stop development environment"
	@echo "  make dev-logs      - View development logs"
	@echo "  make dev-build     - Rebuild and start development environment"
	@echo ""
	@echo "Database:"
	@echo "  make db-connect    - Connect to MySQL database from host"
	@echo "  make db-shell      - Open MySQL shell in container"
	@echo "  make db-shell-dev  - Open MySQL shell in container (dev)"
	@echo ""
	@echo "Other:"
	@echo "  make status        - Show status of all services"
	@echo ""

# Start the application
start:
	@echo "Starting Final Store..."
	@./start.sh

# Stop the application
stop:
	@echo "Stopping Final Store..."
	@docker-compose down

# Restart the application
restart:
	@echo "Restarting Final Store..."
	@docker-compose restart

# View application logs
logs:
	@docker-compose logs -f app

# View all logs
logs-all:
	@docker-compose logs -f

# Rebuild and start
build:
	@echo "Rebuilding and starting Final Store..."
	@docker-compose up -d --build

# Clean everything
clean:
	@echo "Cleaning up Final Store (this will remove all data)..."
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

# Show status
status:
	@docker-compose ps

# Development mode commands
dev:
	@echo "Starting in development mode with hot reload..."
	@./start-dev.sh

dev-stop:
	@echo "Stopping development environment..."
	@docker-compose -f docker-compose.dev.yml down

dev-logs:
	@docker-compose -f docker-compose.dev.yml logs -f app

dev-build:
	@echo "Rebuilding and starting development environment..."
	@docker-compose -f docker-compose.dev.yml up -d --build

