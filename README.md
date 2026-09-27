# NutriGO Backend

Backend del MVP académico NutriGO, construido con Spring Boot, PostgreSQL y arquitectura hexagonal pragmática.

La planificación y fases de desarrollo están en [docs/RUTA_DESARROLLO.md](docs/RUTA_DESARROLLO.md).

## Entorno local

El entorno local usa tres contenedores: backend, PostgreSQL y Keycloak.

1. Instala e inicia Docker Desktop.
2. Copia `.env.example` a `.env` y reemplaza todos los valores `change-this-*` por contraseñas locales seguras.
3. Desde esta carpeta, ejecuta:

   ```powershell
   docker compose up --build
   ```

Cuando los servicios estén listos:

- API y Swagger: `http://localhost:8080/swagger-ui.html`
- Estado de la API: `http://localhost:8080/actuator/health`
- Keycloak: `http://localhost:8081`
- PostgreSQL: `localhost:5433` (mapeado a 5433 porque 5432 puede estar ocupado por un PostgreSQL local del equipo; dentro de la red Docker sigue siendo `postgres:5432`)

El backend queda disponible para depuración remota desde IntelliJ en `localhost:5005`.

### Usuario administrador de desarrollo

El realm `nutrigo` incluye un usuario precargado para pruebas manuales y con Postman (roles `ADMIN` y `USER`):

- Usuario: `admin.dev`
- Contraseña: `AdminDev2026!`

Es solo para desarrollo local. Si cambias `keycloak/nutrigo-realm.json`, Keycloak no reimporta el realm sobre una base existente: ejecuta `docker compose down -v` y `docker compose up --build` para que el cambio tome efecto.

Para detener el entorno sin borrar las bases de datos:

```powershell
docker compose down
```

No uses `docker compose down -v` salvo que quieras eliminar completamente los datos locales de PostgreSQL y Keycloak.
