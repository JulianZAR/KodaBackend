CREATE TABLE usuarios (
    id               UUID PRIMARY KEY,
    keycloak_user_id UUID NOT NULL,
    nombre           VARCHAR(150) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    estatura_cm      NUMERIC(5, 2),
    peso_kg          NUMERIC(5, 2),
    meta             VARCHAR(30),
    fecha_registro   TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_usuarios_keycloak_user_id UNIQUE (keycloak_user_id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);
