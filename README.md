# Movs App

Aplicación académica de catálogo de películas con frontend Vue y API REST Spring Boot. Incluye autenticación JWT, roles `ADMIN` y `USER`, suscripciones, registro público transaccional y administración de usuarios y películas.

## Tecnologías

| Capa | Tecnologías |
|---|---|
| Frontend | Vue 3, Vite 8, Tailwind CSS y Bootstrap |
| Backend | Java 17, Spring Boot 3.5, Spring Security y Spring Data JPA |
| Base de datos | PostgreSQL 17 y HikariCP |
| API | REST, JWT Bearer y Springdoc OpenAPI/Swagger |
| Empaquetado | Maven Wrapper y WAR ejecutable |

## Funcionalidades

- Registro público que crea únicamente usuarios `USER` y su suscripción en una transacción.
- Inicio y cierre de sesión con contraseñas BCrypt y tokens JWT.
- Autorización por roles con respuestas controladas `401` y `403`.
- Catálogo con 1.000 películas precargadas.
- Perfil y suscripción del usuario autenticado.
- Panel `ADMIN` con subsecciones para usuarios y películas.
- CRUD, búsqueda y paginación administrativa de cinco registros por página.
- Navegación directa a primera, anterior, siguiente y última página.
- Swagger UI con esquema `bearerAuth` y botón **Authorize**.
- Interfaz adaptable con modo claro y oscuro.

## Requisitos

- Java 17.
- PostgreSQL 17 o una versión compatible.
- Node.js `20.19+` o `22.12+`.
- `npm`.

## Configuración de PostgreSQL

Los scripts son idempotentes y deben ejecutarse en orden. El primer script requiere un usuario administrador de PostgreSQL:

```bash
sudo -u postgres psql -v ON_ERROR_STOP=1 \
  -f database/00_create_database_and_user.sql

sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
  -f database/01_schema.sql

sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
  -f database/02_seed.sql

sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
  -f database/03_verify.sql
```

`03_verify.sql` muestra los conteos de películas, usuarios, suscripciones y sesiones.

## Variables de entorno

Copia el archivo de ejemplo y reemplaza los valores `CHANGE_ME`:

```bash
cp .env.example .env
```

Antes de iniciar el backend, exporta las variables:

```bash
set -a
source .env
set +a
```

El archivo `.env` contiene credenciales locales y está excluido de Git.

## Ejecutar el backend

Durante el desarrollo:

```bash
./mvnw spring-boot:run
```

También puede generarse y ejecutar el WAR:

```bash
./mvnw clean package
java -jar target/movs-app-backend-0.0.1-SNAPSHOT.war
```

La API queda disponible en `http://localhost:8080`.

## Ejecutar el frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

Abre `http://localhost:5173/movs-app/`.

## Accesos útiles

| Recurso | URL |
|---|---|
| Frontend | `http://localhost:5173/movs-app/` |
| API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

Las cuentas exclusivamente locales y académicas están documentadas en [`users.md`](users.md).

## Estructura principal

```text
database/   Scripts PostgreSQL ordenados
frontend/   Aplicación Vue y panel administrativo
src/        API Spring Boot
users.md    Cuentas académicas locales
```
