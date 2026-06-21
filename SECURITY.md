# Seguridad

## Tabla de contenido

1. [Gobernanza](#1-gobernanza)
2. [Diseño](#2-diseño)
3. [Implementación](#3-implementación)
4. [Verificación](#4-verificación)
5. [Operaciones](#5-operaciones)
6. [Riesgos conocidos](#6-riesgos-conocidos)
7. [Mejoras futuras](#7-mejoras-futuras)

## 1. Gobernanza

Este documento define los controles de seguridad aplicados al backend, la API, la autenticación, las sesiones, la base de datos y la protección de datos de la aplicación.

Este documento toma como referencia OWASP ASVS, Application Security Verification Standard, como estándar de verificación de controles de seguridad en aplicaciones web y APIs.

La documentación se organiza según OWASP SAMM, Software Assurance Maturity Model, como metodología de madurez para estructurar los controles en los dominios de gobernanza, diseño, implementación, verificación y operaciones.

Las credenciales y configuraciones sensibles deben mantenerse fuera del repositorio. El archivo `.env` no debe versionarse y `.env.example` solo debe incluir nombres de variables requeridas, sin valores reales de producción.

Las cuentas demo deben eliminarse, deshabilitarse o rotarse antes de un despliegue en producción.

## 2. Diseño

La aplicación contempla autenticación de usuarios mediante credenciales y validación de contraseñas con `PasswordEncoder`.

La autorización por roles todavía no está implementada. Actualmente se identifica la sesión del usuario, pero se prevé incorporar control de permisos para restringir acciones según el rol.

La gestión de sesiones se apoya en la tabla `sesiones`, que mantiene una fila única por usuario. El inicio y cierre de sesión aplican bloqueo pesimista dentro de una transacción. Un segundo inicio activo devuelve HTTP 409.

La protección de datos se basa en limitar la información expuesta mediante DTO, normalizar el correo y evitar la devolución de contraseñas o hashes en las respuestas.

La configuración CORS se controla mediante `APP_ALLOWED_ORIGINS`, permitiendo restringir los orígenes autorizados desde variables de entorno.

En producción, la aplicación debe desplegarse usando HTTPS/TLS para proteger el tráfico entre cliente y servidor.

## 3. Implementación

La configuración sensible se obtiene de variables de entorno. `.env.example` define los nombres requeridos sin constituir una configuración de producción.

La aplicación no registra credenciales ni devuelve el campo `passwordHash`. Los DTO de respuesta omiten contraseñas y hashes.

El servicio de usuarios genera hashes BCrypt con coste 12. El servicio de autenticación compara la contraseña mediante `PasswordEncoder`.

Jakarta Validation comprueba obligatoriedad, correo, longitudes, rangos y valores positivos. Las restricciones e índices de MySQL refuerzan la integridad.

Las consultas JPA parametrizadas reducen el riesgo de inyección SQL.

El manejador global entrega mensajes controlados. Las respuestas 500 no muestran trazas, consultas ni detalles internos.

## 4. Verificación

La seguridad debe verificarse revisando los controles implementados frente a OWASP ASVS.

Se deben validar los flujos de autenticación, cierre de sesión, sesión única, manejo de errores y protección de datos sensibles.

Las dependencias del proyecto deben revisarse periódicamente para identificar vulnerabilidades conocidas.

También deben probarse las validaciones de entrada, el comportamiento ante datos inválidos y la ausencia de información sensible en las respuestas.

## 5. Operaciones

Los registros de la aplicación no deben incluir contraseñas, hashes, tokens ni información sensible.

Se recomienda registrar eventos relevantes de seguridad, como intentos de inicio de sesión, errores controlados y acciones críticas.

Debe incorporarse límite de frecuencia para reducir el riesgo de ataques de fuerza bruta contra el inicio de sesión.

La base de datos debe contar con mecanismos de respaldo y recuperación antes de pasar a producción.

Debe definirse un canal para reportar vulnerabilidades encontradas en la aplicación.

## 6. Riesgos conocidos

La sesión identifica al usuario mediante su id y no autentica cada solicitud CRUD.

El uso de HTTP sin TLS expone tráfico si se despliega fuera del equipo local.

Las cuentas demo deben eliminarse o rotarse antes de producción.

La autorización por roles aún no está implementada.

## 7. Mejoras futuras

Se prevé incorporar Spring Security, JWT de corta duración, tokens de renovación rotativos, autorización por roles, revocación centralizada, TLS y límites de frecuencia.
