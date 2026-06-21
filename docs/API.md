# Contrato de la API

## Tabla de contenido

1. [Autenticación](#autenticación)
2. [Usuarios](#usuarios)
3. [Planes](#planes)
4. [Suscripciones](#suscripciones)
5. [Películas](#películas)
6. [Errores](#errores)

La base local es `http://localhost:8080`. Todas las solicitudes y respuestas usan `application/json`. OpenAPI constituye la referencia ejecutable en `/v3/api-docs` y Swagger UI la representa en `/swagger-ui.html`.

Salvo `/api/auth/login` y `POST /api/usuarios`, las rutas bajo `/api/**` requieren el encabezado `Authorization: Bearer <token>`.

## Autenticación

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| POST | `/api/auth/login` | `LoginRequest` | `200`, sesión activa |
| POST | `/api/auth/logout` | — | `204` |
| GET | `/api/auth/sesion/{idUsuario}` | — | `200`, estado de sesión |

Una cuenta con sesión activa no admite otro inicio. La API devuelve `409` y el mensaje `El usuario ya tiene una sesión activa. Cierre la sesión anterior antes de iniciar nuevamente.`.

El token devuelto por el login debe enviarse como Bearer token en las solicitudes protegidas. Las operaciones administrativas requieren rol `admin`. La consulta de sesión o suscripción por usuario requiere ser el mismo usuario autenticado o tener rol `admin`.

### Solicitud

```json
{"correo":"ana@example.com","password":"ClaveSegura123"}
```

### Respuesta

```json
{"id":3,"usuarioId":3,"correo":"ana@example.com","rol":"usuario","activa":true,"fechaInicio":"2026-06-21T10:30:00","fechaCierre":null,"token":"<bearer-token>","tokenExpira":"2026-06-21T16:30:00Z"}
```

## Usuarios

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| GET | `/api/usuarios` | — | `200`, lista de usuarios |
| GET | `/api/usuarios/{id}` | — | `200`, usuario |
| GET | `/api/usuarios/rol/{rol}` | — | `200`, lista filtrada |
| GET | `/api/usuarios/me` | — | `200`, perfil del autenticado |
| POST | `/api/usuarios` | `UsuarioRequest` | `201`, usuario creado |
| PUT | `/api/usuarios/{id}` | `UsuarioRequest` | `200`, usuario actualizado |
| DELETE | `/api/usuarios/{id}` | — | `204` |

Salvo `POST /api/usuarios` (público) y `GET /api/usuarios/me` (autenticado), el resto requiere rol `admin`.

### Solicitud

```json
{"nombre":"Ana Pérez","correo":"ana@example.com","password":"ClaveSegura123","rol":"usuario"}
```

### Respuesta

```json
{"id":3,"nombre":"Ana Pérez","correo":"ana@example.com","rol":"usuario"}
```

## Planes

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| GET | `/api/planes` | — | `200`, lista de planes |
| GET | `/api/planes/{id}` | — | `200`, plan |
| POST | `/api/planes` | `PlanRequest` | `201`, plan creado |
| PUT | `/api/planes/{id}` | `PlanRequest` | `200`, plan actualizado |
| DELETE | `/api/planes/{id}` | — | `204` |

`GET` es público; `POST`, `PUT` y `DELETE` requieren rol `admin`.

### Solicitud

```json
{"codigo":"basic","nombre":"Basic","precio":4.99,"duracionDias":30,"beneficios":["1 pantalla","Calidad HD"]}
```

### Respuesta

```json
{"id":1,"codigo":"basic","nombre":"Basic","precio":4.99,"duracionDias":30,"beneficios":["1 pantalla","Calidad HD"]}
```

## Suscripciones

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| GET | `/api/suscripciones` | — | `200`, lista de suscripciones |
| GET | `/api/suscripciones/{id}` | — | `200`, suscripción |
| GET | `/api/suscripciones/usuario/{idUsuario}` | — | `200`, suscripción del usuario |
| POST | `/api/suscripciones` | `SuscripcionRequest` | `201`, suscripción creada |
| PUT | `/api/suscripciones/{id}` | `SuscripcionRequest` | `200`, suscripción actualizada |
| DELETE | `/api/suscripciones/{id}` | — | `204` |

`GET /api/suscripciones/usuario/{idUsuario}` y `POST` requieren ser el mismo usuario o `admin`. El resto requiere `admin`.

### Solicitud

```json
{"usuarioId":3,"planId":1,"fechaInicio":"2026-06-21","estado":"ACTIVA"}
```

### Respuesta

```json
{"id":1,"usuarioId":3,"planId":1,"plan":"Basic","fechaInicio":"2026-06-21","fechaExpiracion":"2026-07-21","estado":"ACTIVA"}
```

## Películas

| Método | Ruta | Solicitud | Respuesta correcta |
|---|---|---|---|
| GET | `/api/peliculas` | — | `200`, lista de películas |
| GET | `/api/peliculas/{id}` | — | `200`, película |
| GET | `/api/peliculas/genero/{genero}` | — | `200`, lista filtrada |
| GET | `/api/peliculas/buscar?titulo=texto` | — | `200`, coincidencias parciales |
| POST | `/api/peliculas` | `PeliculaRequest` | `201`, película creada |
| PUT | `/api/peliculas/{id}` | `PeliculaRequest` | `200`, película actualizada |
| DELETE | `/api/peliculas/{id}` | — | `204` |

`GET` es público; `POST`, `PUT` y `DELETE` requieren rol `admin`.

### Solicitud

```json
{"titulo":"Cover Story","anio":2026,"genero":"Drama","descripcion":"Descripción","imagenUrl":"001-cover.png","variante":"movie-card-featured"}
```

### Respuesta

```json
{"id":1,"titulo":"Cover Story","anio":2026,"genero":"Drama","descripcion":"Descripción","imagenUrl":"001-cover.png","variante":"movie-card-featured"}
```

## Errores

| Código | Condición |
|---:|---|
| 400 | Validación o solicitud inválida |
| 401 | Credenciales incorrectas, token ausente, inválido o expirado |
| 403 | Operación sin permisos para el rol o usuario autenticado |
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
