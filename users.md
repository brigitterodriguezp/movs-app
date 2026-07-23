# Usuarios locales de prueba

| Nombre    | Correo              | Contraseña      | Rol   |
| --------- | ------------------- | --------------- | ----- |
| Admin     | admin@movs.app      | Admin123!       | ADMIN |
| Alejandra | alejandra@gmail.com | alejandra.2005  | USER  |

El seed `db/02_seed.sql` crea ambas cuentas. Al iniciar el backend, la cuenta
administradora también se valida mediante `APP_ADMIN_NAME`, `APP_ADMIN_EMAIL` y
`APP_ADMIN_PASSWORD` configuradas en el archivo local `.env`.

> `.env.example` contiene marcadores de configuración y no credenciales funcionales.
