CREATE TABLE notificaciones (
    id         UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo       VARCHAR(30) NOT NULL,
    titulo     VARCHAR(150) NOT NULL,
    mensaje    VARCHAR(500) NOT NULL,
    leida      BOOLEAN NOT NULL DEFAULT FALSE,
    fecha      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_notificaciones_usuario_fecha ON notificaciones (usuario_id, fecha DESC);
