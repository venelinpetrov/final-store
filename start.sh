#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Final Store - Docker Setup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed.${NC}"
    echo "Please install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}Error: Docker Compose is not installed.${NC}"
    echo "Please install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}No .env file found. Creating from .env.example...${NC}"
    cp .env.example .env
    
    # Generate JWT secret if not set
    if ! grep -q "JWT_SECRET=.\+" .env; then
        echo -e "${YELLOW}Generating JWT secret...${NC}"
        JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
        
        # Update .env file with generated secret
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            sed -i '' "s|JWT_SECRET=|JWT_SECRET=${JWT_SECRET}|" .env
        else
            # Linux
            sed -i "s|JWT_SECRET=|JWT_SECRET=${JWT_SECRET}|" .env
        fi
        echo -e "${GREEN}✓ JWT secret generated and added to .env${NC}"
    fi
else
    echo -e "${GREEN}✓ .env file found${NC}"
    
    # Check if JWT_SECRET is set
    if ! grep -q "JWT_SECRET=.\+" .env; then
        echo -e "${YELLOW}JWT_SECRET not set in .env. Generating...${NC}"
        JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
        
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|JWT_SECRET=|JWT_SECRET=${JWT_SECRET}|" .env
        else
            sed -i "s|JWT_SECRET=|JWT_SECRET=${JWT_SECRET}|" .env
        fi
        echo -e "${GREEN}✓ JWT secret generated and added to .env${NC}"
    fi
fi

echo ""
echo -e "${GREEN}Starting Docker containers...${NC}"
echo ""

# Start Docker Compose
docker-compose up -d

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Application started successfully!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "API is available at: ${GREEN}http://localhost:8080${NC}"
    echo -e "Swagger UI: ${GREEN}http://localhost:8080/swagger-ui.html${NC}"
    echo ""
    echo -e "To view logs: ${YELLOW}docker-compose logs -f app${NC}"
    echo -e "To stop: ${YELLOW}docker-compose down${NC}"
    echo ""
else
    echo -e "${RED}Failed to start Docker containers.${NC}"
    exit 1
fi

