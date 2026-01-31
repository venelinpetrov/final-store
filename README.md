# Final Store

A comprehensive Java Spring Boot e-commerce store API.

## Project Links

- [Roadmap](https://github.com/users/venelinpetrov/projects/2/views/1)
- [Database design](https://github.com/venelinpetrov/db-my-store)
- [Seed script](https://github.com/venelinpetrov/my-store-seed-script)

## Documentation

- [Development Guide](DEVELOPMENT.md) - Hot reload, debugging, and workflow
- [Docker Guide](DOCKER.md) - Detailed Docker documentation
- [Quick Start Guide](QUICK_START.md) - Quick reference

## Prerequisites

Choose one of the following setup options:

### Option 1: Local Setup (full control)

**Required:**
- Java 21
- MySQL 8.0
- Python 3 (for seed script)

**Installation (macOS):**
```bash
# Install Java 21
brew install openjdk@21

# Install MySQL
brew install mysql
brew services start mysql
```

**Installation (Ubuntu):**
```bash
# Install Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Install MySQL
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

**Optional:**
- [DBeaver](https://dbeaver.io/download/) for database management
- IntelliJ IDEA for development

### Option 2: Docker Setup (easiest, OS-agnostic)

**Required:**
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

**Use this option if:**
- You don't want to install Java / MySQL locally
- You want an isolated environment

## Setup Instructions

### Local Setup

#### 1. Configure MySQL

```bash
# Connect to MySQL
mysql -u root

# Set root password and create database
ALTER USER 'root'@'localhost' IDENTIFIED BY '0000';
FLUSH PRIVILEGES;
CREATE DATABASE my_store;
EXIT;
```

#### 2. Configure Environment Variables

```bash
# Copy environment template
cp .env.example .env

# Generate JWT secret
openssl rand -base64 64

# Edit .env and set JWT_SECRET to the generated value
```

#### 3. Configure IntelliJ IDEA

**Set up JDK:**
1. Go to File > Project Structure > Project
2. Set SDK to Java 21

Find JDK location if needed:
```bash
# macOS
/usr/libexec/java_home -V

# Ubuntu
ls /usr/lib/jvm
```

Common locations:
- macOS: `/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home`
- Ubuntu: `/usr/lib/jvm/java-21-openjdk-amd64/`

**Set up database connection:**
1. Open Database tab (right side)
2. Click + > Data Source > MySQL
3. Configure:
   - Host: `localhost`
   - Port: `3306`
   - User: `root`
   - Password: `0000`
   - Database: `my_store`

**Run migrations:**
1. Open Maven tool window
2. Navigate to Plugins > flyway
3. Run `flyway:migrate`

**Create run configuration:**
1. Run > Edit Configurations
2. Add new Spring Boot configuration
3. Select main class: `com.vpe.finalstore.FinalStoreApplication`
4. Run the application

#### 4. Seed the Database

Run the seed script from [here](https://github.com/venelinpetrov/my-store-seed-script)

#### 5. Access the Application

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### Docker Setup

Docker Compose sets up both the application and MySQL database with a single command.

#### 1. Configure Environment Variables

```bash
# Copy environment template
cp .env.example .env

# Generate JWT secret
openssl rand -base64 64

# Edit .env and set JWT_SECRET to the generated value
```

#### 2. Start the Application

**Development mode (with hot reload):**
```bash
make dev
```

Features:
- Automatically reloads when you change Java files
- No rebuild needed after code changes
- Debug port available on 5005
- Useful when you don't have Java installed locally

**Production mode:**
```bash
make start
```

Note: In production mode, rebuild after code changes:
```bash
make build
```

**All available commands:**
```bash
make help
```

#### 3. Seed the Database

Connect to MySQL:
```bash
# From Docker container
docker exec -it final-store-mysql mysql -uroot -p0000 my_store

# From host machine
mysql -h 127.0.0.1 -P 3307 -u root -p0000 my_store
```

Run the seed script from [here](https://github.com/venelinpetrov/my-store-seed-script)

#### 4. Access the Application

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Additional Information

### Migrations

Flyway migrations run automatically when the application starts. The database schema is created on first startup.

### Common Tasks

**View logs (Docker):**
```bash
make logs
```

**Stop the application (Docker):**
```bash
make stop          # Production mode
make dev-stop      # Development mode
```

**Database shell (Docker):**
```bash
make db-shell      # Production mode
make db-shell-dev  # Development mode
```
