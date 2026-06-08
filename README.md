# Movs App

Aplicación web SPA para gestionar múltiples cuentas de usuario con suscripciones y explorar un catálogo de películas.

## Tabla de contenido

- [Instalación](#1-instalación)
- [Resumen](#2-resumen)
- [Tecnologías utilizadas](#3-tecnologías-utilizadas)
- [Rutas disponibles](#4-rutas-disponibles)
- [Diagrama de login](#5-diagrama-de-login)

---

## 1. Instalación

a. Clonar o descargar el proyecto.

b. Entrar a la carpeta del proyecto:

```bash
cd P1ExamenRodriguezBrigitte
```

c. Instalar las dependencias:

```bash
npm install
```

d. Ejecutar el servidor de desarrollo:

```bash
npm run dev
```

e. Abrir la URL que muestra Vite en el navegador:

```txt
http://localhost:5173/movs-app/
```

f. Para generar la versión de producción:

```bash
npm run build
```

---

## 2. Resumen

El sistema permite registrar múltiples usuarios con planes de suscripción (Basic \$4.99 o Plus \$8.99). Cada usuario tiene un rol (`admin` o `usuario`). Los administradores pueden gestionar todos los usuarios desde un panel protegido. La membresía tiene caducidad calculada desde el registro. El catálogo de películas se carga desde un archivo JSON con imágenes mapeadas dinámicamente. Todo el almacenamiento es local (localStorage).

### Estructura de datos

- **src/data/users.json** — usuarios de semilla (admin + demo).
- **src/data/plans.json** — planes de suscripción con duración.
- **src/data/movies.json** — películas con título, año, género, descripción e imagen.

---

## 3. Tecnologías utilizadas

| Tecnología   | Versión | Propósito                                        |
| ------------ | ------- | ------------------------------------------------ |
| Vue 3        | ^3.5.32 | Framework de interfaz por componentes            |
| Vite         | ^8.0.8  | Servidor de desarrollo y build                   |
| Vue Router   | ^5.0.4  | Enrutamiento con guardias de autenticación y rol |
| Tailwind CSS | ^4.3.0  | Estilos utilitarios responsive                   |
| Bootstrap    | ^5.3.8  | Componentes base (botones, formularios)          |
| Lucide Vue   | ^1.16.0 | Iconos vectoriales                               |
| localStorage | —       | Almacenamiento de usuarios, sesión y tema        |

---

## 4. Rutas disponibles

| Ruta            | Nombre       | Acceso         | Descripción                             |
| --------------- | ------------ | -------------- | --------------------------------------- |
| `/`             | home         | Público        | Página principal de bienvenida          |
| `/signin`       | signin       | Público        | Inicio de sesión                        |
| `/signup`       | signup       | Público        | Registro de nuevo usuario               |
| `/about`        | about        | Público        | Información del proyecto                |
| `/app`          | accounts     | Usuario logged | Panel de suscripción del usuario actual |
| `/movies`       | movie-app    | Usuario logged | Catálogo de películas con buscador      |
| `/admin`        | admin        | Solo admin     | Panel de administración de usuarios     |
| `/unauthorized` | unauthorized | Público        | Página 403 de acceso no autorizado      |

---

## 5. Diagrama de login

```
                    ┌──────────────┐
                    │   Landing    │
                    │     (/)      │
                    └──────┬───────┘
                           │
              ┌────────────┴────────────┐
              │                         │
       ┌──────▼──────┐          ┌───────▼───────┐
       │   Signup    │          │    Signin     │
       │  /signup    │          │   /signin     │
       └──────┬──────┘          └───────┬───────┘
              │                         │
              │       ┌─────────────────┴──────────────┐
              │       │      Validar credenciales      │
              │       │  (busca en movieUsers array)   │
              │       └────────┬───────────┬───────────┘
              │                │           │
              │          ┌─────▼──┐  ┌─────▼─────┐
              │          │ Admin  │  │  Usuario  │
              │          │ rol=ad │  │ rol=user  │
              │          └───┬────┘  └─────┬─────┘
              │              │             │
              │       ┌──────▼──┐    ┌─────▼──────┐
              │       │ /admin  │    │   /app     │
              │       │ Panel   │    │  Mi cuenta │
              │       │ gestión │    │ suscripción│
              │       └─────────┘    └─────┬──────┘
              │                            │
              │                     ┌──────▼──────┐
              │                     │  /movies    │
              │                     │  Catálogo   │
              │                     └─────────────┘
              │
              └── Guardia de rutas ──
                  - Sin sesión → redirect /signin
                  - /admin sin rol=admin → /unauthorized (403)
```

---
