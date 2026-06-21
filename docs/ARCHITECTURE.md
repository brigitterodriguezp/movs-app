# Arquitectura

## Tabla de contenido

1. [Arquitectura por capas](#arquitectura-por-capas)
2. [Modelo C4 simplificado](#modelo-c4-simplificado)
   - [Nivel 1: Contexto](#nivel-1-contexto)
   - [Nivel 2: Contenedores](#nivel-2-contenedores)
   - [Nivel 3: Componentes](#nivel-3-componentes)
3. [Estructura de carpetas](#estructura-de-carpetas)
4. [Responsabilidad de capas](#responsabilidad-de-capas)
5. [Relaciones entre entidades](#relaciones-entre-entidades)
6. [Decisiones técnicas](#decisiones-técnicas)
7. [Anotaciones JPA](#anotaciones-jpa)
8. [Control de sesión única](#control-de-sesión-única)
9. [Flujo completo API ↔ MySQL](#flujo-completo-api--mysql)

## Arquitectura por capas

El backend aplica dependencias unidireccionales. El controlador traduce HTTP, el servicio concentra reglas y transacciones, el repositorio abstrae JPA y la entidad representa persistencia. Los DTO delimitan el contrato externo. La configuración define componentes transversales y la capa de excepciones normaliza fallos.

```mermaid
flowchart LR
  C[Cliente] --> R[API REST]
  R --> S[Spring Boot]
  S --> D[(MySQL)]
  D --> S --> R --> C
```

## Modelo C4 simplificado

### Nivel 1: Contexto

```mermaid
C4Context
  Person(persona, "Usuario", "Consulta películas y administra su cuenta")
  System(movs, "Movs App", "Gestiona catálogo, membresías y sesiones")
  System_Ext(mysql, "MySQL de LAMPP", "Conserva datos relacionales")
  Rel(persona, movs, "Usa", "HTTPS/JSON")
  Rel(movs, mysql, "Lee y escribe", "JDBC")
```

### Nivel 2: Contenedores

```mermaid
C4Container
  Person(persona, "Usuario")
  Container(spa, "SPA", "Vue 3", "Interfaz web")
  Container(api, "API", "Spring Boot 3.5.15", "Reglas y contrato REST")
  ContainerDb(db, "Base de datos", "MySQL", "Persistencia")
  Rel(persona, spa, "Usa")
  Rel(spa, api, "Solicita", "HTTP/JSON")
  Rel(api, db, "Persiste", "JPA/JDBC")
```

### Nivel 3: Componentes

```mermaid
C4Component
  Container_Boundary(api, "API Spring Boot") {
    Component(controller, "Controllers", "Spring MVC", "Adapta HTTP")
    Component(dto, "DTO", "Jakarta Validation", "Valida el contrato")
    Component(service, "Services", "Spring", "Ejecuta reglas y transacciones")
    Component(repository, "Repositories", "Spring Data JPA", "Accede a datos")
    Component(handler, "GlobalExceptionHandler", "RestControllerAdvice", "Uniforma errores")
  }
  Rel(controller, dto, "Recibe y devuelve")
  Rel(controller, service, "Invoca")
  Rel(service, repository, "Consulta")
  Rel(controller, handler, "Delega errores")
```

## Estructura de carpetas

```text
src/main/java/com/movsapp/backend/
├── config/       configuración CORS, BCrypt y OpenAPI
├── controller/   adaptadores REST
├── dto/          solicitudes y respuestas
├── entity/       modelo JPA
├── exception/    errores controlados
├── repository/   acceso Spring Data
└── service/      reglas, mapeo y transacciones
```

## Responsabilidad de capas

| Capa       | Responsabilidad                                   |
| ---------- | ------------------------------------------------- |
| Controller | Rutas, códigos HTTP y validación de entrada       |
| Service    | Reglas, transacciones y transformación de modelos |
| Repository | Consultas y persistencia                          |
| Entity     | Relaciones y restricciones del dominio persistido |
| DTO        | Contrato sin exposición del modelo interno        |
| Exception  | Respuestas de error uniformes                     |
| Config     | CORS, cifrado de contraseñas y OpenAPI            |

## Relaciones entre entidades

```mermaid
erDiagram
  ROL ||--o{ USUARIO : asigna
  USUARIO ||--o| SUSCRIPCION : posee
  PLAN ||--o{ SUSCRIPCION : define
  USUARIO ||--o| SESION : controla
  PLAN ||--o{ PLAN_BENEFICIO : incluye
  PELICULA { bigint id PK }
```

### Explicación de cada entidad

| Entidad | Propósito | Relaciones | Datos que almacena |
|---|---|---|---|
| `roles` | Catálogo fijo de roles del sistema | Un rol → muchos usuarios | `id`, `nombre` (`admin`, `usuario`) |
| `usuarios` | Representa una cuenta registrada | Muchos usuarios → un rol; un usuario → una suscripción; un usuario → una sesión | `id`, `nombre`, `correo` (único), `password_hash`, `rol_id` |
| `planes` | Planes de suscripción disponibles | Un plan → muchas suscripciones; un plan → muchos beneficios | `id`, `codigo` (único), `nombre` (único), `precio`, `duracion_dias` |
| `plan_beneficios` | Beneficios desglosados de cada plan | Muchos beneficios → un plan | `plan_id`, `orden`, `beneficio` |
| `suscripciones` | Vínculo entre un usuario y un plan | Una suscripción → un usuario; una suscripción → un plan | `id`, `usuario_id` (único), `plan_id`, `fecha_inicio`, `fecha_expiracion`, `estado` |
| `peliculas` | Catálogo de películas del sistema | No tiene FK | `id`, `titulo`, `anio`, `genero`, `descripcion`, `imagen_url`, `variante` |
| `sesiones` | Control de sesión única por usuario | Una sesión → un usuario | `id`, `usuario_id` (único), `activa`, `fecha_inicio`, `fecha_cierre` |

### Claves primarias, foráneas y restricciones

| Entidad           | PK                 | UK                 | FK                                                      | Restricciones                                                                   |
| ----------------- | ------------------ | ------------------ | ------------------------------------------------------- | ------------------------------------------------------------------------------- |
| `roles`           | `id`               | `nombre`           | —                                                       | —                                                                               |
| `usuarios`        | `id`               | `correo`           | `rol_id` → `roles(id)`                                  | —                                                                               |
| `planes`          | `id`               | `codigo`, `nombre` | —                                                       | `precio >= 0`, `duracion_dias > 0`                                              |
| `plan_beneficios` | `(plan_id, orden)` | —                  | `plan_id` → `planes(id)` ON DELETE CASCADE              | —                                                                               |
| `suscripciones`   | `id`               | `usuario_id`       | `usuario_id` → `usuarios(id)`, `plan_id` → `planes(id)` | `fecha_expiracion > fecha_inicio`, `estado IN ('ACTIVA','VENCIDA','CANCELADA')` |
| `peliculas`       | `id`               | —                  | —                                                       | `anio BETWEEN 1888 AND 2100`                                                    |
| `sesiones`        | `id`               | `usuario_id`       | `usuario_id` → `usuarios(id)` ON DELETE CASCADE         | —                                                                               |

## Decisiones técnicas

La API utiliza DTO para impedir que el hash forme parte de la serialización. JPA mantiene el modelo y `schema.sql` controla el DDL evaluable. La aplicación no valida el esquema durante el arranque para evitar caída si MySQL no está disponible; la validación del DDL se realiza de forma manual o en pruebas. CORS proviene del entorno. La fecha de expiración deriva de la fecha inicial y la duración vigente del plan.

## Anotaciones JPA

| Anotación | Ámbito | Por qué | Para qué |
|---|---|---|---|
| `@Entity` | Todas las entidades | Obligatoria de JPA | Marca la clase como entidad persistible |
| `@Table` | Todas las entidades | Personalizar el DDL generado | Nombrar tabla, UKs e índices en MySQL (ej. `uk_usuarios_correo`, `idx_peliculas_genero`) |
| `@Id` + `@GeneratedValue(IDENTITY)` | Todas las entidades | Estándar para PK autoincremental | Delega el autoincremento a MySQL (`AUTO_INCREMENT`) |
| `@Column` | Todos los campos | Control fino del esquema | `nullable`, `length`, `name`, `precision`/`scale` para que JPA genere el DDL exacto |
| `@ManyToOne(fetch=LAZY)` | `Usuario.rol`, `Suscripcion.plan`, `Suscripcion.usuario` | Relación N:1 con carga diferida | `Usuario.rol` y `Suscripcion.plan` — evita traer el objeto completo si no se necesita |
| `@OneToOne(fetch=LAZY)` | `Suscripcion.usuario`, `Sesion.usuario` | Relación 1:1 con carga diferida | Restricción de fila única por usuario |
| `@JoinColumn` | Todas las FK | Nombrar la columna FK | `rol_id`, `usuario_id`, `plan_id` — coincide con el DDL de `schema.sql` |
| `@ForeignKey` | Todas las FK | Nombrar la restricción FK | `fk_usuarios_roles`, `fk_suscripciones_planes` — DDL explícito y mantenible |
| `@Enumerated(STRING)` | `Suscripcion.estado` | Persistencia legible de enums | `EstadoSuscripcion` se guarda como `'ACTIVA'`/`'VENCIDA'` en vez de ordinal numérico |
| `@ElementCollection` + `@CollectionTable` | `Plan.beneficios` | Mapear lista sin entidad propia | `beneficios` → tabla `plan_beneficios` con FK a `planes` |
| `@OrderColumn` | `Plan.beneficios` | Preservar orden de lista | Columna `orden` para mantener la posición de cada beneficio |
| `@UniqueConstraint` | `@Table` | Definir restricciones UK en el DDL | `uk_usuarios_correo`, `uk_planes_codigo`, `uk_suscripciones_usuario` |
| `@Index` | `@Table` | Definir índices en el DDL | `idx_peliculas_titulo`, `idx_usuarios_rol` — rendimiento en búsquedas |

## Control de sesión única

La restricción única de `sesiones.usuario_id` limita cada cuenta a un estado de sesión. El servicio bloquea esa fila con `PESSIMISTIC_WRITE`, comprueba `activa` y responde con conflicto antes de reabrirla. El cierre conserva la fecha de inicio, registra la fecha de cierre y permite el siguiente acceso.

## Flujo completo API ↔ MySQL

```mermaid
sequenceDiagram
  participant C as Cliente (Vue)
  participant API as API REST (Spring Boot)
  participant DB as MySQL

  rect rgb(200, 240, 200)
    Note over C,DB: Registro
    C->>API: POST /api/usuarios
    API->>DB: INSERT INTO usuarios
    DB-->>API: fila creada
    API-->>C: 201 usuario
  end

  rect rgb(200, 220, 255)
    Note over C,DB: Login
    C->>API: POST /api/auth/login
    API->>DB: SELECT FROM usuarios WHERE correo=?
    API->>DB: INSERT INTO sesiones
    DB-->>API: sesión activa + token
    API-->>C: 200 sesión con token
  end

  rect rgb(255, 230, 200)
    Note over C,DB: CRUD autenticado
    C->>API: GET /api/usuarios/me<br>Authorization: Bearer token
    API->>DB: SELECT FROM usuarios WHERE id=?
    DB-->>API: datos de usuario
    API-->>C: 200 usuario

    C->>API: GET /api/peliculas
    API->>DB: SELECT * FROM peliculas
    DB-->>API: lista de películas
    API-->>C: 200 películas

    C->>API: GET /api/suscripciones/usuario/{id}
    API->>DB: SELECT FROM suscripciones WHERE usuario_id=?
    DB-->>API: suscripción vigente
    API-->>C: 200 suscripción
  end

  rect rgb(255, 200, 200)
    Note over C,DB: Logout
    C->>API: POST /api/auth/logout
    API->>DB: UPDATE sesiones SET activa=0, fecha_cierre=NOW()
    DB-->>API: fila actualizada
    API-->>C: 204
  end
```
