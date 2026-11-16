-- =============================================================================
-- Datos de prueba SOLO para desarrollo local (perfil Spring "dev").
-- Los keycloak_user_id coinciden con los "id" de los usuarios definidos en
-- keycloak/nutrigo-realm.json. Contraseña de todos: NutriGO2026!
--
--   laura.dev@nutrigo.local         USER          perfil completo, 5 pesajes y plan
--   nutricionista.dev@nutrigo.local NUTRITIONIST  perfil con estatura
--   nuevo.dev@nutrigo.local         USER          sin datos (para probar el alta)
--   admin.dev                       ADMIN, USER   se crea solo al iniciar sesión
--
-- Migración repetible e idempotente: se puede volver a ejecutar sin duplicar.
-- =============================================================================

INSERT INTO usuarios (id, keycloak_user_id, nombre, email, estatura_cm, peso_kg, meta, fecha_registro)
VALUES
    ('1a2b0000-0000-4000-8000-000000000003', '0b8f5a1e-0000-4000-8000-000000000003',
     'Laura Gómez', 'laura.dev@nutrigo.local', 165.00, 75.20, 'PERDER_PESO', now() - interval '60 days'),
    ('1a2b0000-0000-4000-8000-000000000002', '0b8f5a1e-0000-4000-8000-000000000002',
     'Marta Rincón', 'nutricionista.dev@nutrigo.local', 168.00, NULL, NULL, now() - interval '90 days')
ON CONFLICT DO NOTHING;

-- IMC calculado igual que CalculadoraIMC (estatura 165 cm).
INSERT INTO registros_progreso (id, usuario_id, peso_kg, imc, fecha)
VALUES
    ('5e1d0000-0000-4000-8000-000000000001', '1a2b0000-0000-4000-8000-000000000003', 78.40, 28.80, now() - interval '56 days'),
    ('5e1d0000-0000-4000-8000-000000000002', '1a2b0000-0000-4000-8000-000000000003', 77.60, 28.50, now() - interval '42 days'),
    ('5e1d0000-0000-4000-8000-000000000003', '1a2b0000-0000-4000-8000-000000000003', 76.90, 28.25, now() - interval '28 days'),
    ('5e1d0000-0000-4000-8000-000000000004', '1a2b0000-0000-4000-8000-000000000003', 76.10, 27.95, now() - interval '14 days'),
    ('5e1d0000-0000-4000-8000-000000000005', '1a2b0000-0000-4000-8000-000000000003', 75.20, 27.62, now() - interval '1 days')
ON CONFLICT DO NOTHING;

INSERT INTO planes_nutricionales (id, usuario_id, nombre, objetivo, descripcion, fecha_asignacion)
VALUES
    ('7c3e0000-0000-4000-8000-000000000001', '1a2b0000-0000-4000-8000-000000000003',
     'Plan de pérdida de peso - fase 1', 'PERDER_PESO',
     E'Déficit calórico moderado (~400 kcal/día).\nProteína: 1.6 g por kg de peso.\nVerduras en almuerzo y cena; carbohidratos integrales.\nCaminar 30 minutos, 5 días por semana.',
     now() - interval '50 days')
ON CONFLICT DO NOTHING;
