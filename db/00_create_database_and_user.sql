-- Ejecutar con psql como el usuario administrador postgres.
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'brigitte') THEN
    CREATE ROLE brigitte LOGIN PASSWORD 'brigitte.2005' NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
  ELSE
    ALTER ROLE brigitte WITH LOGIN PASSWORD 'brigitte.2005' NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
  END IF;
END
$$;

SELECT 'CREATE DATABASE movs_app_db OWNER postgres ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'movs_app_db')\gexec

REVOKE ALL ON DATABASE movs_app_db FROM PUBLIC;
GRANT CONNECT ON DATABASE movs_app_db TO brigitte;
