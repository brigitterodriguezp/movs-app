# Seguridad

## Variables de entorno

La configuración sensible se obtiene de variables de entorno. `.env.example` define los nombres requeridos sin constituir una configuración de producción. El archivo `.env` permanece excluido del repositorio.

## Protección de credenciales

La aplicación no registra credenciales ni devuelve el campo `passwordHash`. Los DTO de respuesta omiten contraseñas y hashes.

## BCrypt

El servicio de usuarios genera hashes BCrypt con coste 12. El servicio de autenticación compara la contraseña mediante `PasswordEncoder`.

## Control de sesión única

La tabla `sesiones` mantiene una fila única por usuario. El inicio y cierre aplican bloqueo pesimista dentro de una transacción. Un segundo inicio activo devuelve HTTP 409.

## Validaciones

Jakarta Validation comprueba obligatoriedad, correo, longitudes, rangos y valores positivos. Las restricciones e índices de MySQL refuerzan la integridad.

## CORS

La variable `APP_ALLOWED_ORIGINS` restringe los orígenes. La configuración admite una lista separada por comas.

## Manejo de errores

El manejador global entrega mensajes controlados. Las respuestas 500 no muestran trazas, consultas ni detalles internos.

## Protección de datos

Los servicios normalizan el correo, usan DTO y limitan la información expuesta. Las consultas JPA parametrizadas reducen el riesgo de inyección SQL.

## Riesgos conocidos

La sesión identifica al usuario mediante su id y no autentica cada solicitud CRUD. El uso de HTTP sin TLS expone tráfico si se despliega fuera del equipo local. Las cuentas demo deben eliminarse o rotarse antes de producción.

## Mejoras futuras

Se prevé incorporar Spring Security, JWT de corta duración, tokens de renovación rotativos, autorización por roles, revocación centralizada, TLS y límites de frecuencia.
