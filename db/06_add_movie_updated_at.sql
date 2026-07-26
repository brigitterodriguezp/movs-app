\connect movs_app_db

ALTER TABLE peliculas
  ADD COLUMN IF NOT EXISTS actualizada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_peliculas_actualizada
  ON peliculas (actualizada_en DESC);
