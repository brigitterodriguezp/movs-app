# Movs App Backend

## Tabla de contenido

1. [Resumen](#1-resumen)
2. [Tecnologías usadas](#2-tecnologías-usadas)
3. [Instalación](#3-instalación)

## 1. Resumen

Movs App Backend expone una API REST para usuarios, planes, suscripciones, sesiones y catálogo de películas. La aplicación persiste los datos en MySQL.

## 2. Tecnologías usadas

| Tecnología | Versión exacta | Propósito |
|---|---:|---|
| Java | 21 | Plataforma de ejecución |
| Spring Boot | 3.5.15 | Framework de aplicación |
| Maven | 3.9.16 | Construcción y dependencias |
| Springdoc OpenAPI | 2.8.14 | Contrato OpenAPI y Swagger UI |
| MySQL | 8.0 o compatible | Persistencia relacional |

## 3. Instalación

1. Se clona el repositorio y se accede a su raíz.
2. Se inicia MySQL desde LAMPP.
3. Se ejecuta `docs/database/schema.sql` en MySQL.
4. Se copia `.env.example` a `.env`, se ajustan sus valores y se exportan con `set -a; source .env; set +a`.
5. Se ejecuta `./mvnw spring-boot:run`.
6. Se abre `http://localhost:8080/swagger-ui.html`.
