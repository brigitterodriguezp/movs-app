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
