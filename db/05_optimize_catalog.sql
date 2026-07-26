\connect movs_app_db

-- Búsquedas parciales case-insensitive usadas por /api/peliculas/pagina.
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_peliculas_titulo_trgm
  ON peliculas USING GIN (lower(titulo) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_peliculas_genero_trgm
  ON peliculas USING GIN (lower(genero) gin_trgm_ops);

-- Listado paginado de favoritos ordenado por fecha para un usuario.
CREATE INDEX IF NOT EXISTS idx_favoritos_usuario_fecha
  ON favoritos (usuario_id, fecha_agregada DESC) INCLUDE (pelicula_id);

-- Actualiza las estadísticas del planificador después de cargas masivas.
CREATE OR REPLACE PROCEDURE optimizar_catalogo()
LANGUAGE plpgsql
AS $$
BEGIN
  ANALYZE peliculas;
  ANALYZE favoritos;
END;
$$;

CALL optimizar_catalogo();
