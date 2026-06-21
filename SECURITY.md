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

| ID | Control | Descripción | Estado |
|---|---|---|---|
| D-01 | Autenticación | La aplicación contempla autenticación de usuarios mediante credenciales y validación de contraseñas con `PasswordEncoder`. | Implementado |
| D-02 | Autorización | Aunque existen roles de usuario, la autorización por roles todavía no está implementada en el backend. | Pendiente |
| D-03 | Gestión de sesiones | La tabla `sesiones` mantiene una fila única por usuario. El inicio y cierre de sesión aplican bloqueo pesimista dentro de una transacción. | Implementado parcial |
| D-04 | Control de sesión única | Un segundo inicio activo devuelve HTTP 409 para impedir sesiones simultáneas del mismo usuario. | Implementado |
| D-05 | Protección de datos | Se limita la información expuesta mediante DTO, normalización de correo y omisión de contraseñas o hashes en respuestas. | Implementado |
| D-06 | CORS | La configuración CORS se controla mediante `APP_ALLOWED_ORIGINS`, permitiendo restringir orígenes autorizados desde variables de entorno. | Implementado |
| D-07 | HTTPS/TLS | En producción, la aplicación debe desplegarse usando HTTPS/TLS para proteger el tráfico entre cliente y servidor. | Pendiente |

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

## 4. Verificación

| ID | Verificación | Descripción | Estado |
|---|---|---|---|
| V-01 | Revisión contra OWASP ASVS | La seguridad debe verificarse revisando los controles implementados frente a OWASP ASVS. | Pendiente |
| V-02 | Pruebas de autenticación | Se deben validar los flujos de inicio de sesión, credenciales inválidas y cierre de sesión. | Implementado parcial |
| V-03 | Pruebas de sesión única | Se debe verificar que un segundo inicio activo devuelva HTTP 409. | Implementado parcial |
| V-04 | Pruebas de manejo de errores | Se debe comprobar que los errores controlados no expongan trazas, consultas ni detalles internos. | Pendiente |
| V-05 | Pruebas de protección de datos | Se debe verificar que las respuestas no incluyan contraseñas, hashes ni información sensible innecesaria. | Pendiente |
| V-06 | Pruebas de validación de entrada | Se debe comprobar el comportamiento ante datos inválidos, campos obligatorios y formatos incorrectos. | Pendiente |
| V-07 | Revisión de dependencias | Las dependencias del backend y frontend deben revisarse periódicamente para identificar vulnerabilidades conocidas. | Pendiente |

## 5. Operaciones

| ID | Control operativo | Descripción | Estado |
|---|---|---|---|
| O-01 | Protección de logs | Los registros de la aplicación no deben incluir contraseñas, hashes, tokens ni información sensible. | Definido |
| O-02 | Eventos de seguridad | Se recomienda registrar intentos de inicio de sesión, errores controlados y acciones críticas. | Pendiente |
| O-03 | Límite de frecuencia | Debe incorporarse rate limiting para reducir ataques de fuerza bruta contra el inicio de sesión. | Pendiente |
| O-04 | Respaldo y recuperación | La base de datos debe contar con mecanismos de respaldo y recuperación antes de pasar a producción. | Pendiente |
| O-05 | Reporte de vulnerabilidades | Debe definirse un canal para reportar vulnerabilidades encontradas en la aplicación. | Pendiente |

## 6. Riesgos conocidos

| ID | Riesgo | Impacto | Estado | Mitigación prevista |
|---|---|---|---|---|
| R-01 | La API todavía no cuenta con autenticación por solicitud. | Los endpoints CRUD pueden ser consumidos sin validar la identidad del usuario en cada petición. | Pendiente | Implementar Spring Security con JWT, cookies seguras o un mecanismo equivalente. |
| R-02 | La autorización por roles aún no está implementada en el backend. | Las restricciones aplicadas solo desde el frontend pueden ser omitidas al consumir la API directamente. | Pendiente | Implementar autorización por roles y permisos en backend. |
| R-03 | El cierre y la consulta de sesión dependen del identificador del usuario. | Puede existir riesgo de suplantación si se manipula el `idUsuario`. | Pendiente | Asociar las operaciones de sesión a un token o contexto autenticado. |
| R-04 | Las sesiones no tienen expiración automática documentada. | Una sesión activa depende del cierre manual. | Pendiente | Incorporar expiración, revocación y renovación controlada de sesiones. |
| R-05 | El uso de HTTP sin TLS fuera de local expone el tráfico. | Credenciales y datos pueden viajar sin cifrado en entornos no seguros. | Pendiente | Exigir HTTPS/TLS en producción. |
| R-06 | No existe límite de frecuencia para el inicio de sesión. | El endpoint de login puede ser más vulnerable a intentos repetidos de fuerza bruta. | Pendiente | Implementar rate limiting y bloqueo temporal ante intentos fallidos. |
| R-07 | No existe una estrategia formal de auditoría y monitoreo de seguridad. | Puede dificultarse la detección de actividad sospechosa o incidentes. | Pendiente | Registrar eventos críticos y definir alertas de seguridad. |
| R-08 | No se documenta un proceso periódico de revisión de dependencias. | Vulnerabilidades conocidas en librerías pueden pasar desapercibidas. | Pendiente | Incorporar revisión periódica con herramientas de análisis de dependencias. |
| R-09 | Las cuentas demo pueden permanecer activas antes de producción. | Cuentas conocidas o débiles pueden facilitar accesos no autorizados. | Pendiente | Eliminar, deshabilitar o rotar cuentas demo antes del despliegue. |

## 7. Mejoras futuras

| ID | Mejora | Relacionado con | Prioridad | Descripción |
|---|---|---|---|---|
| M-01 | Implementar Spring Security | R-01, R-02 | Alta | Incorporar una capa formal de seguridad para autenticar solicitudes y proteger endpoints. |
| M-02 | Usar JWT de corta duración | R-01, R-03, R-04 | Alta | Emitir tokens con expiración limitada para reducir el impacto de sesiones comprometidas. |
| M-03 | Incorporar tokens de renovación rotativos | R-04 | Media | Permitir renovación controlada de sesión reduciendo el riesgo de reutilización de tokens. |
| M-04 | Aplicar autorización por roles | R-02 | Alta | Restringir operaciones según rol, por ejemplo `admin` o `usuario`, desde el backend. |
| M-05 | Agregar revocación centralizada | R-03, R-04 | Media | Permitir invalidar sesiones o tokens activos desde el servidor. |
| M-06 | Exigir HTTPS/TLS en producción | R-05 | Alta | Proteger credenciales y datos transmitidos entre cliente y servidor. |
| M-07 | Implementar límites de frecuencia | R-06 | Media | Reducir intentos repetidos de inicio de sesión y ataques de fuerza bruta. |
| M-08 | Definir auditoría y monitoreo | R-07 | Media | Registrar eventos de seguridad y habilitar alertas ante actividad sospechosa. |
| M-09 | Automatizar revisión de dependencias | R-08 | Media | Revisar periódicamente librerías del backend y frontend para detectar vulnerabilidades conocidas. |
| M-10 | Gestionar cuentas demo antes de producción | R-09 | Alta | Eliminar, deshabilitar o rotar credenciales demo antes de desplegar en producción. |
