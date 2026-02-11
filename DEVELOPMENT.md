# Development Guide

This guide explains how to develop the Final Store application using Docker, even without Java installed locally.

## Two Modes Available

### 1. Development Mode

**Features:**
- Hot reload - changes are reflected automatically
- No rebuild needed after code changes
- Remote debugging support (port 5005)
- Works without local Java installation
- Fast feedback loop

**Start:**
```bash
make dev
```

**How it works:**
- Your source code is mounted into the container
- Spring Boot DevTools watches for changes
- When you save a file, the app automatically restarts (~5-10 seconds)
- No need to rebuild the Docker image

### 2. Production Mode

**Features:**
- Optimized Docker image
- Multi-stage build for smaller size
- Production-ready configuration

**Start:**
```bash
make start
```

**Note:** Requires rebuild after code changes:
```bash
make build
```

## Remote Debugging

The development mode exposes port 5005 for remote debugging.

### IntelliJ IDEA Setup:

1. Go to **Run** → **Edit Configurations**
2. Click **+** → **Remote JVM Debug**
3. Set:
   - Name: `Docker Debug`
   - Host: `localhost`
   - Port: `5005`
4. Click **OK**
5. Click the debug icon to attach

### VS Code Setup:

Add to `.vscode/launch.json`:
```json
{
  "type": "java",
  "name": "Attach to Docker",
  "request": "attach",
  "hostName": "localhost",
  "port": 5005
}
```

## Common Commands

```bash
# Development mode
make dev              # Start development environment
make dev-stop         # Stop development environment
make dev-logs         # View logs
make dev-build        # Rebuild (after pom.xml changes)

# Production mode
make start            # Start production environment
make stop             # Stop
make logs             # View logs
make build            # Rebuild

# Database
make db-shell         # Open MySQL shell
make db-connect       # Connect from host

# Migrations (Local - port 3306)
make migrate          # Run Flyway migrations
make migrate-info     # Show migration status
make migrate-repair   # Repair migration metadata
make migrate-clean    # Clean database (WARNING: deletes all data)

# Migrations (Dev Docker - port 3307)
make migrate-dev        # Run Flyway migrations on dev database
make migrate-info-dev   # Show migration status on dev database
make migrate-repair-dev # Repair migration metadata on dev database
make migrate-clean-dev  # Clean dev database (WARNING: deletes all data)

# Other
make status           # Show container status
make help             # Show all commands
```

## Troubleshooting

### Changes not appearing?

1. **Check if the app restarted:**
   ```bash
   make dev-logs
   ```
   Look for "Restarting due to class path change"

2. **Hard refresh Swagger:**
   - Chrome/Firefox: Cmd+Shift+R (Mac) or Ctrl+Shift+R (Windows/Linux)

3. **Restart the container:**
   ```bash
   docker-compose -f docker-compose.dev.yml restart app
   ```

### App won't start?

1. **Check logs:**
   ```bash
   make dev-logs
   ```

2. **Check for compilation errors:**
   The logs will show Maven compilation errors

3. **Rebuild from scratch:**
   ```bash
   make dev-stop
   make dev-build
   ```

### Port conflicts?

Edit `.env` file:
```bash
MYSQL_PORT=3308  # Change MySQL port
```

For app port, edit `docker-compose.dev.yml`:
```yaml
ports:
  - "8081:8080"  # Change to 8081
```

### App crashes on startup with "Flyway validation failed"?

This happens when a migration fails mid-execution, leaving the database in a corrupted state.

**Fix:**
1. Stop the dev environment:
   ```bash
   make dev-stop
   ```

2. Start only MySQL container:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d mysql
   ```

3. Repair the Flyway schema:
   ```bash
   make migrate-repair-dev
   ```

4. Restart the full environment:
   ```bash
   make dev
   ```

**Note:** Flyway migrations run automatically when the app starts in Docker. You don't need to run migrations manually unless you're troubleshooting or running the app locally outside Docker.

## What's Next?

- Check out [DOCKER.md](DOCKER.md) for more Docker details
- See [README.md](README.md) for general setup instructions

