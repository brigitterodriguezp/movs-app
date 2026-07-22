\connect movs_app_db

INSERT INTO roles (id, nombre) VALUES (1, 'ADMIN'), (2, 'USER')
ON CONFLICT (id) DO UPDATE SET nombre = EXCLUDED.nombre;

-- Normaliza cuentas académicas heredadas a proveedores de correo reconocibles.
-- La distribución conserva la mayoría en Gmail y una muestra en Outlook.
UPDATE usuarios
SET correo = split_part(correo, '@', 1) || '@' ||
  CASE WHEN id % 5 = 0 THEN 'outlook.com' ELSE 'gmail.com' END
WHERE lower(split_part(correo, '@', 2)) IN ('movs.test', 'movs.app');

-- Sustituye identidades genéricas heredadas por nombres completos y correos coherentes.
WITH catalogo AS (
  SELECT
    ARRAY[
      'Sofia','Mateo','Valentina','Sebastian','Camila',
      'Nicolas','Daniela','Gabriel','Martina','Samuel',
      'Isabella','Julian','Emilia','Leonardo','Mariana',
      'Tomas','Renata','Andres','Paula','Felipe',
      'Natalia','Diego','Carolina','Martin','Elena'
    ]::text[] AS nombres,
    ARRAY['Garcia','Rodriguez','Lopez','Martinez','Gonzalez']::text[] AS apellidos
), identidades AS (
  SELECT
    u.id,
    c.nombres[((u.id - 1) % 25 + 1)::integer] AS nombre,
    c.apellidos[(((u.id - 1) / 25) % 5 + 1)::integer] AS apellido
  FROM usuarios u
  CROSS JOIN catalogo c
  WHERE u.nombre ~* '^(administración|administracion|usuario( [0-9]+)?)$'
     OR lower(split_part(u.correo, '@', 1)) ~ '^(admin|usuario[0-9]*)$'
)
UPDATE usuarios u
SET nombre = i.nombre || ' ' || i.apellido,
    correo = lower(i.nombre) || '.' || lower(i.apellido) || '@' ||
      CASE WHEN u.id % 5 = 0 THEN 'outlook.com' ELSE 'gmail.com' END
FROM identidades i
WHERE u.id = i.id;

INSERT INTO planes (id, codigo, nombre, precio, duracion_dias) VALUES
  (1, 'basic', 'Basic', 4.99, 30),
  (2, 'plus', 'Plus', 8.99, 30)
ON CONFLICT (id) DO UPDATE SET codigo = EXCLUDED.codigo, nombre = EXCLUDED.nombre,
  precio = EXCLUDED.precio, duracion_dias = EXCLUDED.duracion_dias;

INSERT INTO plan_beneficios (plan_id, orden, beneficio) VALUES
  (1,0,'1 pantalla'),(1,1,'Catálogo esencial'),(1,2,'Calidad HD'),
  (2,0,'3 pantallas'),(2,1,'Estrenos destacados'),(2,2,'Full HD y favoritos')
ON CONFLICT (plan_id, orden) DO UPDATE SET beneficio = EXCLUDED.beneficio;

INSERT INTO categorias (id, nombre) VALUES
  (1,'Drama'),(2,'Suspenso'),(3,'Terror'),(4,'Crimen'),
  (5,'Misterio'),(6,'Biografía'),(7,'Romance'),(8,'Comedia'),
  (9,'Acción'),(10,'Ciencia ficción'),(11,'Documental')
ON CONFLICT (id) DO UPDATE SET nombre = EXCLUDED.nombre;

INSERT INTO peliculas (id,titulo,anio,genero,descripcion,imagen_url,variante,categoria_id) VALUES
 (1,'Cover Story',2026,'Drama','Una historia íntima para volver cuando la noche pide calma.','001-cover.png','movie-card-featured',1),
 (2,'La Niñera',2025,'Suspenso','Una casa tranquila empieza a guardar demasiados secretos.','002-ninera.png','movie-card-tall',2),
 (3,'Scary Movie',2024,'Terror','Risas oscuras, sustos rápidos y una noche imposible de pausar.','003-scary-movie.png',NULL,3),
 (4,'Little Women',2023,'Drama','Decisiones grandes en habitaciones pequeñas.','004-little-women.png',NULL,1),
 (5,'Joker',2022,'Crimen','Una mirada intensa a una ciudad que ya no sabe escuchar.','005-joker.png','movie-card-wide',4),
 (6,'The Frightening',2021,'Misterio','Algo se mueve entre pasillos donde nadie debería estar.','006-the-frightening.png',NULL,5),
 (7,'Marilyn Monroe',2020,'Biografía','Luz, cámara y una silueta que nunca dejó de aparecer.','007-marilyn-monroe.png',NULL,6),
 (8,'Love Untangled',2025,'Romance','Primer amor, nervios y una confesión esperando su momento.','009-love-untangled.png','movie-card-wide',7)
ON CONFLICT (id) DO UPDATE SET titulo=EXCLUDED.titulo, anio=EXCLUDED.anio, genero=EXCLUDED.genero,
 descripcion=EXCLUDED.descripcion, imagen_url=EXCLUDED.imagen_url, variante=EXCLUDED.variante, categoria_id=EXCLUDED.categoria_id;

INSERT INTO peliculas (id, titulo, anio, genero, descripcion, imagen_url, variante, categoria_id)
SELECT n,
       'Película de catálogo ' || lpad(n::text, 4, '0'),
       1980 + (n % 46)::integer,
       (ARRAY['Drama','Comedia','Acción','Ciencia ficción','Documental'])[1 + (n % 5)::integer],
       'Película generada para validar el catálogo académico de Movs App.',
       'catalogo-' || lpad(n::text, 4, '0') || '.png',
       NULL,
       (ARRAY[1,8,9,10,11])[1 + (n % 5)::integer]
FROM generate_series(9, 1000) AS n
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('roles','id'), (SELECT MAX(id) FROM roles), true);
SELECT setval(pg_get_serial_sequence('planes','id'), (SELECT MAX(id) FROM planes), true);
SELECT setval(pg_get_serial_sequence('categorias','id'), (SELECT MAX(id) FROM categorias), true);
SELECT setval(pg_get_serial_sequence('peliculas','id'), (SELECT MAX(id) FROM peliculas), true);
