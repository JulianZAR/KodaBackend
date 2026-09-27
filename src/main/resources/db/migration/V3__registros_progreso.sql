CREATE TABLE registros_progreso (
    id         UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    peso_kg    NUMERIC(5, 2) NOT NULL,
    imc        NUMERIC(5, 2) NOT NULL,
    fecha      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_registros_progreso_usuario_fecha ON registros_progreso (usuario_id, fecha DESC);
