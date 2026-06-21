CREATE DATABASE IF NOT EXISTS movs_app_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE movs_app_db;

CREATE TABLE roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL,
  CONSTRAINT pk_roles PRIMARY KEY (id),
  CONSTRAINT uk_roles_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(120) NOT NULL,
  correo VARCHAR(160) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  rol_id BIGINT NOT NULL,
  CONSTRAINT pk_usuarios PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_correo UNIQUE (correo),
  CONSTRAINT fk_usuarios_roles FOREIGN KEY (rol_id) REFERENCES roles(id)
) ENGINE=InnoDB;
CREATE INDEX idx_usuarios_rol ON usuarios (rol_id);

CREATE TABLE planes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(30) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  duracion_dias INT NOT NULL,
  CONSTRAINT pk_planes PRIMARY KEY (id),
  CONSTRAINT uk_planes_codigo UNIQUE (codigo),
  CONSTRAINT uk_planes_nombre UNIQUE (nombre),
  CONSTRAINT chk_planes_precio CHECK (precio >= 0),
  CONSTRAINT chk_planes_duracion CHECK (duracion_dias > 0)
) ENGINE=InnoDB;

CREATE TABLE plan_beneficios (
  plan_id BIGINT NOT NULL,
  orden INT NOT NULL,
  beneficio VARCHAR(180) NOT NULL,
  CONSTRAINT pk_plan_beneficios PRIMARY KEY (plan_id, orden),
  CONSTRAINT fk_beneficios_planes FOREIGN KEY (plan_id) REFERENCES planes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE suscripciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  plan_id BIGINT NOT NULL,
  fecha_inicio DATE NOT NULL,
  fecha_expiracion DATE NOT NULL,
  estado VARCHAR(20) NOT NULL,
  CONSTRAINT pk_suscripciones PRIMARY KEY (id),
  CONSTRAINT uk_suscripciones_usuario UNIQUE (usuario_id),
  CONSTRAINT fk_suscripciones_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  CONSTRAINT fk_suscripciones_planes FOREIGN KEY (plan_id) REFERENCES planes(id),
  CONSTRAINT chk_suscripciones_fechas CHECK (fecha_expiracion > fecha_inicio),
  CONSTRAINT chk_suscripciones_estado CHECK (estado IN ('ACTIVA','VENCIDA','CANCELADA'))
) ENGINE=InnoDB;
CREATE INDEX idx_suscripciones_plan ON suscripciones (plan_id);

CREATE TABLE peliculas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(160) NOT NULL,
  anio INT NOT NULL,
  genero VARCHAR(60) NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  imagen_url VARCHAR(255) NOT NULL,
  variante VARCHAR(60),
  CONSTRAINT pk_peliculas PRIMARY KEY (id),
  CONSTRAINT chk_peliculas_anio CHECK (anio BETWEEN 1888 AND 2100)
) ENGINE=InnoDB;
CREATE INDEX idx_peliculas_titulo ON peliculas (titulo);
CREATE INDEX idx_peliculas_genero ON peliculas (genero);

CREATE TABLE sesiones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  activa BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_inicio DATETIME(6) NOT NULL,
  fecha_cierre DATETIME(6),
  CONSTRAINT pk_sesiones PRIMARY KEY (id),
  CONSTRAINT uk_sesiones_usuario UNIQUE (usuario_id),
  CONSTRAINT fk_sesiones_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_sesiones_activa ON sesiones (activa);

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
