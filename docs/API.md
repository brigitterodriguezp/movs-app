# Contrato de la API

## Tabla de contenido

1. [Recursos](#recursos)
2. [Autenticación](#autenticación)
3. [Solicitudes JSON](#solicitudes-json)
4. [Respuestas JSON](#respuestas-json)
5. [Errores](#errores)

La base local es `http://localhost:8080`. Todas las solicitudes y respuestas usan `application/json`. OpenAPI constituye la referencia ejecutable en `/v3/api-docs` y Swagger UI la representa en `/swagger-ui.html`.

## Recursos

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| GET | `/api/usuarios` | — | `200`, lista de usuarios |
| GET | `/api/usuarios/{id}` | — | `200`, usuario |
| GET | `/api/usuarios/rol/{rol}` | — | `200`, lista filtrada |
| POST | `/api/usuarios` | `UsuarioRequest` | `201`, usuario |
| PUT | `/api/usuarios/{id}` | `UsuarioRequest` | `200`, usuario |
| DELETE | `/api/usuarios/{id}` | — | `204` |
| GET | `/api/planes` | — | `200`, lista de planes |
| GET | `/api/planes/{id}` | — | `200`, plan |
| POST | `/api/planes` | `PlanRequest` | `201`, plan |
| PUT | `/api/planes/{id}` | `PlanRequest` | `200`, plan |
| DELETE | `/api/planes/{id}` | — | `204` |
| GET | `/api/suscripciones` | — | `200`, lista de suscripciones |
| GET | `/api/suscripciones/{id}` | — | `200`, suscripción |
| GET | `/api/suscripciones/usuario/{idUsuario}` | — | `200`, suscripción del usuario |
| POST | `/api/suscripciones` | `SuscripcionRequest` | `201`, suscripción |
| PUT | `/api/suscripciones/{id}` | `SuscripcionRequest` | `200`, suscripción |
| DELETE | `/api/suscripciones/{id}` | — | `204` |
| GET | `/api/peliculas` | — | `200`, lista de películas |
| GET | `/api/peliculas/{id}` | — | `200`, película |
| GET | `/api/peliculas/genero/{genero}` | — | `200`, lista filtrada |
| GET | `/api/peliculas/buscar?titulo=texto` | — | `200`, coincidencias parciales |
| POST | `/api/peliculas` | `PeliculaRequest` | `201`, película |
| PUT | `/api/peliculas/{id}` | `PeliculaRequest` | `200`, película |
| DELETE | `/api/peliculas/{id}` | — | `204` |

## Autenticación

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| POST | `/api/auth/login` | `LoginRequest` | `200`, sesión activa |
| POST | `/api/auth/logout` | `LogoutRequest` | `204` |
| GET | `/api/auth/sesion/{idUsuario}` | — | `200`, estado de sesión |

Una cuenta con sesión activa no admite otro inicio. La API devuelve `409` y el mensaje `El usuario ya tiene una sesión activa. Cierre la sesión anterior antes de iniciar nuevamente.`.

## Solicitudes JSON

```json
{"nombre":"Ana Pérez","correo":"ana@example.com","password":"ClaveSegura123","rol":"usuario"}
```

```json
{"codigo":"basic","nombre":"Basic","precio":4.99,"duracionDias":30,"beneficios":["1 pantalla","Calidad HD"]}
```

```json
{"usuarioId":3,"planId":1,"fechaInicio":"2026-06-21","estado":"ACTIVA"}
```

```json
{"titulo":"Cover Story","anio":2026,"genero":"Drama","descripcion":"Descripción","imagenUrl":"001-cover.png","variante":"movie-card-featured"}
```

```json
{"correo":"ana@example.com","password":"ClaveSegura123"}
```

```json
{"idUsuario":3}
```

## Respuestas JSON

La respuesta de usuario nunca contiene contraseña ni hash:

```json
{"id":3,"nombre":"Ana Pérez","correo":"ana@example.com","rol":"usuario"}
```

La respuesta de sesión identifica su vigencia:

```json
{"id":3,"usuarioId":3,"correo":"ana@example.com","rol":"usuario","activa":true,"fechaInicio":"2026-06-21T10:30:00","fechaCierre":null}
```

## Errores

| Código | Condición |
|---:|---|
| 400 | Validación o solicitud inválida |
| 401 | Credenciales incorrectas |
| 403 | Operación sin permisos cuando se incorpore autorización por recurso |
| 404 | Recurso inexistente |
| 409 | Correo, código, suscripción o sesión duplicada; restricción de integridad |
| 500 | Error interno controlado |

```json
{
  "timestamp":"2026-06-21T10:30:00-05:00",
  "status":400,
  "error":"Bad Request",
  "mensaje":"La solicitud contiene campos inválidos.",
  "ruta":"/api/usuarios",
  "validaciones":{"correo":"debe ser una dirección de correo electrónico con formato correcto"}
}
```
