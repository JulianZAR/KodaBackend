CREATE TABLE planes_nutricionales (
    id               UUID PRIMARY KEY,
    usuario_id       UUID NOT NULL UNIQUE REFERENCES usuarios (id) ON DELETE CASCADE,
    nombre           VARCHAR(150) NOT NULL,
    objetivo         VARCHAR(30) NOT NULL,
    descripcion      TEXT,
    fecha_asignacion TIMESTAMPTZ NOT NULL
);

CREATE TABLE reportes (
    id         UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo       VARCHAR(30) NOT NULL,
    contenido  TEXT NOT NULL,
    fecha      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_reportes_usuario_fecha ON reportes (usuario_id, fecha DESC);
