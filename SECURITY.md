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

La variable `APP_SECURITY_JWT_SECRET` debe definirse con un valor fuerte y privado antes de ejecutar la aplicación fuera de desarrollo.

## 2. Diseño

| ID | Control | Descripción | Estado |
|---|---|---|---|
| D-01 | Autenticación | La aplicación contempla autenticación de usuarios mediante credenciales y validación de contraseñas con `PasswordEncoder`. | Implementado |
| D-02 | Autenticación por solicitud | Las rutas `/api/**`, excepto login y registro inicial de usuario, requieren `Authorization: Bearer <token>`. | Implementado |
| D-03 | Autorización | El backend valida roles mediante controles de autorización para operaciones administrativas. | Implementado |
| D-04 | Gestión de sesiones | La tabla `sesiones` mantiene una fila única por usuario. El inicio y cierre de sesión aplican bloqueo pesimista dentro de una transacción. | Implementado |
| D-05 | Control de sesión única | Un segundo inicio activo devuelve HTTP 409 para impedir sesiones simultáneas del mismo usuario. | Implementado |
| D-06 | Tokens de acceso | El inicio de sesión emite un token firmado con expiración configurable para validar solicitudes posteriores. | Implementado |
| D-07 | Protección de datos | Se limita la información expuesta mediante DTO, normalización de correo y omisión de contraseñas o hashes en respuestas. | Implementado |
| D-08 | CORS | La configuración CORS se controla mediante `APP_ALLOWED_ORIGINS`, permitiendo restringir orígenes autorizados desde variables de entorno. | Implementado |
| D-09 | HTTPS/TLS | La aplicación permite exigir HTTPS mediante `APP_SECURITY_REQUIRE_HTTPS` en producción. | Implementado configurable |

## 3. Implementación

| ID | Control implementado | Descripción | Estado |
|---|---|---|---|
| I-01 | Variables de entorno | La configuración sensible se obtiene de variables de entorno. `.env.example` define los nombres requeridos sin valores reales de producción. | Implementado |
| I-02 | Protección de credenciales | La aplicación no registra credenciales ni devuelve el campo `passwordHash`. Los DTO de respuesta omiten contraseñas y hashes. | Implementado |
| I-03 | Hash de contraseñas | El servicio de usuarios genera hashes BCrypt con coste 12. El servicio de autenticación compara la contraseña mediante `PasswordEncoder`. | Implementado |
| I-04 | Validaciones de entrada | Jakarta Validation comprueba obligatoriedad, correo, longitudes, rangos y valores positivos. | Implementado |
| I-05 | Integridad en base de datos | Las restricciones e índices de MySQL refuerzan la integridad de los datos. | Implementado |
| I-06 | Consultas parametrizadas | Las consultas JPA parametrizadas reducen el riesgo de inyección SQL. | Implementado |
| I-07 | Manejo seguro de errores | El manejador global entrega mensajes controlados. Las respuestas 500 no muestran trazas, consultas ni detalles internos. | Implementado |
| I-08 | Validación de tokens | Un interceptor valida firma, expiración, sesión activa y rol antes de permitir acceso a rutas protegidas. | Implementado |
| I-09 | Configuración de seguridad | La expiración de tokens, secreto de firma, rate limiting y exigencia de HTTPS se controlan mediante variables de entorno. | Implementado |

## 4. Verificación

| ID | Verificación | Descripción | Estado |
|---|---|---|---|
| V-01 | Revisión contra OWASP ASVS | La seguridad debe verificarse revisando los controles implementados frente a OWASP ASVS. | Pendiente |
| V-02 | Pruebas de autenticación | Se validan credenciales inválidas y rechazo de segunda sesión activa. | Implementado parcial |
| V-03 | Pruebas de sesión única | Se verifica que un segundo inicio activo devuelva HTTP 409. | Implementado |
| V-04 | Pruebas de manejo de errores | Se debe comprobar que los errores controlados no expongan trazas, consultas ni detalles internos. | Pendiente |
| V-05 | Pruebas de protección de datos | Se debe verificar que las respuestas no incluyan contraseñas, hashes ni información sensible innecesaria. | Pendiente |
| V-06 | Pruebas de validación de entrada | Se debe comprobar el comportamiento ante datos inválidos, campos obligatorios y formatos incorrectos. | Pendiente |
| V-07 | Revisión de dependencias | Las dependencias del backend y frontend deben revisarse periódicamente para identificar vulnerabilidades conocidas. | Pendiente |
| V-08 | Pruebas de autorización | Se deben agregar pruebas para rutas protegidas, roles administrativos y acceso propio por usuario. | Pendiente |

## 5. Operaciones

| ID | Control operativo | Descripción | Estado |
|---|---|---|---|
| O-01 | Protección de logs | Los registros de la aplicación no deben incluir contraseñas, hashes, tokens ni información sensible. | Definido |
| O-02 | Eventos de seguridad | Se registran inicios de sesión exitosos, intentos fallidos y cierres de sesión sin contraseñas ni tokens. | Implementado básico |
| O-03 | Límite de frecuencia | El login aplica límite configurable de intentos por origen y correo. | Implementado básico |
| O-04 | Respaldo y recuperación | La base de datos debe contar con mecanismos de respaldo y recuperación antes de pasar a producción. | Pendiente |
| O-05 | Reporte de vulnerabilidades | Debe definirse un canal para reportar vulnerabilidades encontradas en la aplicación. | Pendiente |
| O-06 | HTTPS en producción | `APP_SECURITY_REQUIRE_HTTPS` permite rechazar solicitudes HTTP cuando se active en despliegues productivos. | Implementado configurable |

## 6. Riesgos conocidos

| ID | Riesgo | Impacto | Estado | Mitigación prevista |
|---|---|---|---|---|
| R-01 | El secreto de firma tiene un valor por defecto solo apto para desarrollo. | Si no se cambia en producción, los tokens podrían quedar expuestos a falsificación. | Pendiente de configuración | Definir `APP_SECURITY_JWT_SECRET` con un valor fuerte y privado en producción. |
| R-02 | El rate limiting implementado es en memoria. | En despliegues con varias instancias, cada instancia tendría su propio contador. | Riesgo residual | Usar Redis, base de datos o gateway/API manager para rate limiting distribuido. |
| R-03 | No existen tokens de renovación rotativos. | El usuario debe volver a iniciar sesión cuando expire el token de acceso. | Riesgo aceptado temporal | Incorporar refresh tokens rotativos si se requiere una experiencia de sesión prolongada. |
| R-04 | HTTPS depende de la configuración del despliegue. | Si `APP_SECURITY_REQUIRE_HTTPS` no se activa en producción, podría aceptarse tráfico HTTP. | Pendiente de configuración | Activar `APP_SECURITY_REQUIRE_HTTPS=true` y terminar TLS en proxy/servidor seguro. |
| R-05 | No se documenta todavía un proceso periódico de revisión de dependencias. | Vulnerabilidades conocidas en librerías pueden pasar desapercibidas. | Pendiente | Incorporar revisión periódica con herramientas de análisis de dependencias. |
| R-06 | Las cuentas demo pueden permanecer activas antes de producción. | Cuentas conocidas o débiles pueden facilitar accesos no autorizados. | Pendiente operativo | Eliminar, deshabilitar o rotar cuentas demo antes del despliegue. |
| R-07 | No existe estrategia documentada de respaldo y recuperación. | Una pérdida o corrupción de datos podría dificultar la continuidad del servicio. | Pendiente operativo | Definir política de backups, periodicidad, cifrado y pruebas de restauración. |
| R-08 | No existe canal formal de reporte de vulnerabilidades. | Hallazgos de seguridad podrían no llegar al equipo responsable. | Pendiente operativo | Publicar canal de contacto y procedimiento de respuesta. |

## 7. Mejoras futuras

| ID | Mejora | Relacionado con | Prioridad | Descripción |
|---|---|---|---|---|
| M-01 | Migrar a Spring Security completo | R-01, R-03 | Media | Reemplazar o complementar la seguridad personalizada con filtros y configuración estándar de Spring Security. |
| M-02 | Incorporar tokens de renovación rotativos | R-03 | Media | Permitir renovación controlada de sesión reduciendo el riesgo de reutilización de tokens. |
| M-03 | Usar rate limiting distribuido | R-02 | Media | Centralizar los contadores de intentos usando Redis, base de datos o gateway. |
| M-04 | Automatizar revisión de dependencias | R-05 | Media | Revisar periódicamente librerías del backend y frontend para detectar vulnerabilidades conocidas. |
| M-05 | Gestionar cuentas demo antes de producción | R-06 | Alta | Eliminar, deshabilitar o rotar credenciales demo antes de desplegar en producción. |
| M-06 | Formalizar respaldos y recuperación | R-07 | Alta | Definir política de backups, cifrado, retención y pruebas de restauración. |
| M-07 | Definir canal de reporte de vulnerabilidades | R-08 | Media | Publicar un correo o proceso para recibir, clasificar y responder hallazgos de seguridad. |
| M-08 | Ampliar pruebas de seguridad | V-04, V-05, V-06, V-08 | Alta | Agregar pruebas automatizadas para errores, datos sensibles, validaciones, tokens y roles. |
