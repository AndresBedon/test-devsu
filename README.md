# Devsu Bank

## Tecnologías

- **Backend:** Java Spring Boot 3.5, PostgreSQL, JPA/Hibernate
- **Frontend:** Angular 21
- **Contenedores:** Docker

## Requisitos

- Docker Desktop instalado
- Git

## Instrucciones de despliegue

### 1. Clonar el repositorio

```bash
git clone https://github.com/AndresBedon/test-devsu.git
cd test-devsu
```

### 2. Levantar con Docker

```bash
docker-compose up --build -d
```

### 3. Verificar contenedores

```bash
docker ps
```

### 4. Acceder a la aplicación

- **Frontend:** http://localhost:4200
- **Backend:** http://localhost:8080

## Endpoints principales

- GET/POST/PUT/DELETE `/clientes`
- GET/POST/PUT/DELETE `/cuentas`
- GET/POST/DELETE `/movimientos`
- GET `/reportes?clienteId=1&fechaInicio=2026-01-01&fechaFin=2026-12-31`

## Pruebas

### Backend

```bash
cd backend
mvn test
```

### Frontend

```bash
cd frontend
npm test
```
