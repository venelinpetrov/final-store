# Quick Start Guide

## For Active Development (No Java Required)

### Start Development Mode

```bash
make dev
```

### Make Changes
1. Edit your Java files
2. Save (Cmd+S / Ctrl+S)
3. Wait ~5-10 seconds
4. Refresh Swagger: http://localhost:8080/swagger-ui.html
5. Your changes are live!

### View Logs

```bash
make dev-logs
```

### Stop

```bash
make dev-stop
```

---

## For Production Testing

### Start

```bash
make start
```

### After Code Changes

```bash
make build
```

### Stop

```bash
make stop
```

---

## Access Points

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3307 (user: root, password: 0000)
- **Debug Port** (dev mode): 5005

---

## All Commands

```bash
make help
```

---

## First Time Setup

The scripts automatically:
- Create `.env` file
- Generate JWT secret
- Start MySQL
- Run migrations

Just run `make dev` and you're ready to code.

