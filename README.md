# Movs App

## Tabla de contenido

1. [Resumen](#1-resumen)
2. [Tecnologías usadas](#2-tecnologías-usadas)
3. [Instalación](#3-instalación)
4. [Opciones por rol](#4-opciones-por-rol)
5. [APIs disponibles](#5-apis-disponibles)

## 1. Resumen

Movs App es una aplicación académica para administrar usuarios, planes, suscripciones, sesiones y un catálogo de 1.000 películas. Incluye un frontend en Vue, una API REST en Spring Boot, persistencia en PostgreSQL, autenticación JWT y roles `ADMIN` y `USER`.

## 2. Tecnologías usadas

| Tecnología | Versión exacta | Uso |
|---|---:|---|
| Java | 17.0.12 | Ejecución del backend |
| Apache Maven | 3.9.9 | Compilación y empaquetado WAR |
| Spring Boot | 3.5.15 | API REST y configuración principal |
| Springdoc OpenAPI | 2.8.14 | Swagger UI y contrato OpenAPI |
| PostgreSQL | 17.10 | Base de datos relacional |
| Node.js | 24.13.0 | Ejecución del frontend |
| Vue | 3.5.34 | Interfaz web |
| Vue Router | 5.0.7 | Navegación del frontend |
| Vite | 8.0.13 | Servidor y compilación del frontend |
| Tailwind CSS | 4.3.0 | Estilos de la interfaz |
| Bootstrap | 5.3.8 | Componentes y utilidades visuales |
| Lucide Vue | 1.16.0 | Iconos |

## 3. Instalación

1. Clonar el repositorio y entrar al proyecto:

   ```bash
   git clone git@github.com:brigitterodriguezp/movs-app.git
   cd movs-app
   ```

2. Iniciar PostgreSQL:

   ```bash
   sudo systemctl start postgresql
   ```

3. Crear la base de datos y el usuario técnico:

   ```bash
   sudo -u postgres psql -v ON_ERROR_STOP=1 \
     -f database/00_create_database_and_user.sql
   ```

4. Crear el esquema, cargar los datos y verificar los conteos:

   ```bash
   sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
     -f database/01_schema.sql

   sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
     -f database/02_seed.sql

   sudo -u postgres psql -v ON_ERROR_STOP=1 -d movs_app_db \
     -f database/03_verify.sql
   ```

5. Crear el archivo local de variables de entorno:

   ```bash
   cp .env.example .env
   ```

   Reemplazar los valores `CHANGE_ME` dentro de `.env`.

6. Exportar las variables e iniciar el backend:

   ```bash
   set -a
   source .env
   set +a
   mvn spring-boot:run
   ```

7. En otra terminal, instalar e iniciar el frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

8. Abrir la aplicación y Swagger:

   ```text
   Frontend: http://localhost:5173/movs-app/
   Swagger:  http://localhost:8080/swagger-ui.html
   ```

## 4. Opciones por rol

| Opción | Público | USER | ADMIN |
|---|:---:|:---:|:---:|
| Consultar planes y películas | Sí | Sí | Sí |
| Registrar una cuenta con rol `USER` | Sí | — | — |
| Iniciar sesión | Sí | — | — |
| Cerrar sesión | No | Sí | Sí |
| Consultar perfil propio | No | Sí | Sí |
| Consultar suscripción propia | No | Sí | Sí |
| Administrar usuarios | No | No | Sí |
| Administrar suscripciones | No | No | Sí |
| Crear, editar y eliminar planes | No | No | Sí |
| Crear, editar y eliminar películas | No | No | Sí |
| Acceder al Panel de administración | No | No | Sí |

## 5. APIs disponibles

### 5.1. Autenticación y registro

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Iniciar sesión y obtener JWT |
| `POST` | `/api/auth/logout` | USER / ADMIN | Cerrar la sesión activa |
| `GET` | `/api/auth/sesion/{idUsuario}` | Propio / ADMIN | Consultar una sesión |
| `POST` | `/api/registro` | Público | Registrar un usuario `USER` con suscripción |

### 5.2. Usuarios

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/usuarios/me` | USER / ADMIN | Consultar el perfil autenticado |
| `GET` | `/api/usuarios` | ADMIN | Listar usuarios |
| `GET` | `/api/usuarios/{id}` | ADMIN | Consultar un usuario |
| `GET` | `/api/usuarios/rol/{rol}` | ADMIN | Filtrar usuarios por rol |
| `POST` | `/api/usuarios` | ADMIN | Crear un usuario |
| `PUT` | `/api/usuarios/{id}` | ADMIN | Actualizar un usuario |
| `DELETE` | `/api/usuarios/{id}` | ADMIN | Eliminar un usuario |

### 5.3. Planes

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/planes` | Público | Listar planes |
| `GET` | `/api/planes/{id}` | Público | Consultar un plan |
| `POST` | `/api/planes` | ADMIN | Crear un plan |
| `PUT` | `/api/planes/{id}` | ADMIN | Actualizar un plan |
| `DELETE` | `/api/planes/{id}` | ADMIN | Eliminar un plan |

### 5.4. Suscripciones

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/suscripciones` | ADMIN | Listar suscripciones |
| `GET` | `/api/suscripciones/{id}` | ADMIN | Consultar una suscripción |
| `GET` | `/api/suscripciones/usuario/{idUsuario}` | Propio / ADMIN | Consultar una suscripción por usuario |
| `POST` | `/api/suscripciones` | Propio / ADMIN | Crear una suscripción |
| `PUT` | `/api/suscripciones/{id}` | ADMIN | Actualizar una suscripción |
| `DELETE` | `/api/suscripciones/{id}` | ADMIN | Eliminar una suscripción |

### 5.5. Películas

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/peliculas` | Público | Listar películas |
| `GET` | `/api/peliculas/{id}` | Público | Consultar una película |
| `GET` | `/api/peliculas/genero/{genero}` | Público | Filtrar películas por género |
| `GET` | `/api/peliculas/buscar?titulo={titulo}` | Público | Buscar películas por título |
| `POST` | `/api/peliculas` | ADMIN | Crear una película |
| `PUT` | `/api/peliculas/{id}` | ADMIN | Actualizar una película |
| `DELETE` | `/api/peliculas/{id}` | ADMIN | Eliminar una película |

### 5.6. Documentación

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/v3/api-docs` | Público | Consultar OpenAPI en JSON |
| `GET` | `/swagger-ui.html` | Público | Abrir Swagger UI |
