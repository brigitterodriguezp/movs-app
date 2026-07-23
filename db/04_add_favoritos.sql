\connect movs_app_db

-- Migración incremental: no recrea tablas ni vuelve a cargar el catálogo.
CREATE TABLE IF NOT EXISTS favoritos (
  usuario_id BIGINT NOT NULL,
  pelicula_id BIGINT NOT NULL,
  fecha_agregada TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_favoritos PRIMARY KEY (usuario_id, pelicula_id),
  CONSTRAINT fk_favoritos_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  CONSTRAINT fk_favoritos_peliculas FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_favoritos_pelicula ON favoritos (pelicula_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON favoritos TO brigitte;
