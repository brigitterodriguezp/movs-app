# Seguridad

## Tabla de contenido

1. [Variables de entorno](#1-variables-de-entorno)
2. [Protección de credenciales](#2-protección-de-credenciales)
3. [BCrypt](#3-bcrypt)
4. [Control de sesión única](#4-control-de-sesión-única)
5. [Validaciones](#5-validaciones)
6. [CORS](#6-cors)
7. [Manejo de errores](#7-manejo-de-errores)
8. [Protección de datos](#8-protección-de-datos)
9. [Riesgos conocidos](#9-riesgos-conocidos)
10. [Mejoras futuras](#10-mejoras-futuras)

## 1. Variables de entorno

La configuración sensible se obtiene de variables de entorno. `.env.example` define los nombres requeridos sin constituir una configuración de producción. El archivo `.env` permanece excluido del repositorio.

## 2. Protección de credenciales

La aplicación no registra credenciales ni devuelve el campo `passwordHash`. Los DTO de respuesta omiten contraseñas y hashes.

## 3. BCrypt

El servicio de usuarios genera hashes BCrypt con coste 12. El servicio de autenticación compara la contraseña mediante `PasswordEncoder`.

## 4. Control de sesión única

La tabla `sesiones` mantiene una fila única por usuario. El inicio y cierre aplican bloqueo pesimista dentro de una transacción. Un segundo inicio activo devuelve HTTP 409.

## 5. Validaciones

Jakarta Validation comprueba obligatoriedad, correo, longitudes, rangos y valores positivos. Las restricciones e índices de MySQL refuerzan la integridad.

## 6. CORS

La variable `APP_ALLOWED_ORIGINS` restringe los orígenes. La configuración admite una lista separada por comas.

## 7. Manejo de errores

El manejador global entrega mensajes controlados. Las respuestas 500 no muestran trazas, consultas ni detalles internos.

## 8. Protección de datos

Los servicios normalizan el correo, usan DTO y limitan la información expuesta. Las consultas JPA parametrizadas reducen el riesgo de inyección SQL.

## 9. Riesgos conocidos

La sesión identifica al usuario mediante su id y no autentica cada solicitud CRUD. El uso de HTTP sin TLS expone tráfico si se despliega fuera del equipo local. Las cuentas demo deben eliminarse o rotarse antes de producción.

## 10. Mejoras futuras

Se prevé incorporar Spring Security, JWT de corta duración, tokens de renovación rotativos, autorización por roles, revocación centralizada, TLS y límites de frecuencia.
