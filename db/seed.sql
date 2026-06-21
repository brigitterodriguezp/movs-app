USE movs_app_db;

INSERT INTO roles (id, nombre) VALUES (1, 'admin'), (2, 'usuario')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
INSERT INTO planes (id, codigo, nombre, precio, duracion_dias) VALUES
  (1, 'basic', 'Basic', 4.99, 30), (2, 'plus', 'Plus', 8.99, 30)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), precio=VALUES(precio), duracion_dias=VALUES(duracion_dias);
INSERT INTO plan_beneficios (plan_id, orden, beneficio) VALUES
  (1,0,'1 pantalla'),(1,1,'Catálogo esencial'),(1,2,'Calidad HD'),
  (2,0,'3 pantallas'),(2,1,'Estrenos destacados'),(2,2,'Full HD y favoritos')
ON DUPLICATE KEY UPDATE beneficio=VALUES(beneficio);
INSERT INTO usuarios (id, nombre, correo, password_hash, rol_id) VALUES
  (1,'Brigitte Rodriguez','barodriguez7@espe.edu.ec','$2b$12$f30v7G/1BFmBsXog6TMnfuryKE4zH32TVkBymIbcHp4Dm5tbStHd2',1),
  (2,'Usuario Demo','usuario@movs.app','$2b$12$prQHZF6FY918j8DSx1iteu5D40PndPNGNwsk3MtfIplrhaArfxe7a',2)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), rol_id=VALUES(rol_id);
INSERT INTO suscripciones (id, usuario_id, plan_id, fecha_inicio, fecha_expiracion, estado) VALUES
  (1,1,2,'2026-06-08','2026-07-08','ACTIVA'), (2,2,1,CURRENT_DATE,DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY),'ACTIVA')
ON DUPLICATE KEY UPDATE plan_id=VALUES(plan_id), fecha_inicio=VALUES(fecha_inicio), fecha_expiracion=VALUES(fecha_expiracion), estado=VALUES(estado);
INSERT INTO peliculas (id,titulo,anio,genero,descripcion,imagen_url,variante) VALUES
 (1,'Cover Story',2026,'Drama','Una historia íntima para volver cuando la noche pide calma.','001-cover.png','movie-card-featured'),
 (2,'La Niñera',2025,'Suspenso','Una casa tranquila empieza a guardar demasiados secretos.','002-ninera.png','movie-card-tall'),
 (3,'Scary Movie',2024,'Terror','Risas oscuras, sustos rápidos y una noche imposible de pausar.','003-scary-movie.png',NULL),
 (4,'Little Women',2023,'Drama','Decisiones grandes en habitaciones pequeñas.','004-little-women.png',NULL),
 (5,'Joker',2022,'Crimen','Una mirada intensa a una ciudad que ya no sabe escuchar.','005-joker.png','movie-card-wide'),
 (6,'The Frightening',2021,'Misterio','Algo se mueve entre pasillos donde nadie debería estar.','006-the-frightening.png',NULL),
 (7,'Marilyn Monroe',2020,'Biografía','Luz, cámara y una silueta que nunca dejó de aparecer.','007-marilyn-monroe.png',NULL),
 (8,'Love Untangled',2025,'Romance','Primer amor, nervios y una confesión esperando su momento.','009-love-untangled.png','movie-card-wide')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo), anio=VALUES(anio), genero=VALUES(genero), descripcion=VALUES(descripcion), imagen_url=VALUES(imagen_url), variante=VALUES(variante);
INSERT INTO sesiones (usuario_id, activa, fecha_inicio, fecha_cierre) VALUES
 (1,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(2,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE activa=FALSE, fecha_cierre=CURRENT_TIMESTAMP;
