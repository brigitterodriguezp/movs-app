# Estado de implementación — Movs App

## Instrucciones para el siguiente agente

1. Leer este archivo antes de modificar el proyecto.
2. Continuar desde la primera tarea pendiente.
3. Programar todos los cambios antes de iniciar la aplicación.
4. Evitar reinicios repetidos.
5. Actualizar este archivo antes de terminar la sesión.
6. No modificar README.md ni documentación no solicitada.
7. No realizar commit ni push.

## Implementación

- [x] Migrar dependencias de MySQL a PostgreSQL.
- [x] Configurar conexión mediante usuario técnico.
- [x] Crear `database/00_create_database_and_user.sql`.
- [x] Crear `database/01_schema.sql`.
- [x] Crear `database/02_seed.sql`.
- [x] Crear `database/03_verify.sql`.
- [x] Cargar al menos 1.000 películas.
- [x] Configurar HikariCP.
- [x] Configurar logs seguros.
- [x] Agregar Spring Security.
- [x] Crear `SecurityFilterChain`.
- [x] Crear filtro Bearer JWT.
- [x] Implementar roles `ADMIN` y `USER`.
- [x] Configurar respuestas 401 y 403.
- [x] Impedir selección de rol en el registro público.
- [x] Restringir la administración de usuarios a `ADMIN`.
- [x] Crear `POST /api/registro`.
- [x] Implementar registro transaccional de usuario y suscripción.
- [x] Crear administrador inicial desde variables de entorno.
- [x] Crear usuario de prueba Alejandra con rol `USER`.
- [x] Crear `users.md`.
- [x] Agregar Bearer Authorize a Swagger.
- [x] Configurar empaquetado WAR.
- [x] Adaptar la clase principal para WAR.
- [x] Actualizar `.env.example`.
- [x] Mantener `.env` excluido de Git.

## Validación final

- [x] Ejecutar `mvn clean test`.
- [x] Ejecutar `mvn clean package`.
- [x] Confirmar archivo WAR en `target/`.
- [x] Comprobar conexión con PostgreSQL.
- [x] Comprobar inicio de HikariCP.
- [x] Comprobar administrador inicial.
- [x] Comprobar usuario USER.
- [x] Probar login de ADMIN.
- [x] Probar login de USER.
- [x] Probar respuesta 401.
- [x] Probar respuesta 403.
- [x] Probar registro transaccional.
- [x] Confirmar mínimo 1.000 películas.
- [x] Confirmar botón Authorize en Swagger.

## Último avance

- Agente: Codex
- Fecha: 2026-07-13
- Trabajo realizado: implementación y validación final completas
- Archivos modificados: configuración, backend, pruebas, scripts `database/`, `.env.example`, `users.md`
- Validación ejecutada: sí; 6 pruebas, WAR, PostgreSQL, HTTP y OpenAPI correctos
- Bloqueos: PostgreSQL del sistema requiere sudo; se validó con instancia local temporal

## Siguiente paso

Trabajo completo; usar los scripts `database/` al instalar en el PostgreSQL local definitivo.
