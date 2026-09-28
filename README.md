# NutriGO Backend

Backend de la aplicación NutriGO - Sistema de seguimiento nutricional personalizado con Spring Boot, PostgreSQL y arquitectura hexagonal.

## 🌳 Estructura de Ramas

Este repositorio implementa un flujo de trabajo Git con tres ramas principales:

### 📋 Ramas

- **`desarrollo`**: Rama de desarrollo activo donde se integran todas las nuevas funcionalidades
- **`preproduccion`**: Rama de pruebas pre-producción para validación final antes de desplegar
- **`produccion`**: Rama de producción que contiene el código estable desplegado

### 🔄 Flujo de Trabajo

```
desarrollo → preproduccion → produccion
```

1. Los desarrolladores trabajan en `desarrollo`
2. Cuando una versión está lista, se fusiona a `preproduccion` para testing
3. Después de validar en preproducción, se fusiona a `produccion` para despliegue

## 🚀 Tecnologías

- **Spring Boot** 3.5.16
- **Java** 21
- **PostgreSQL** 17
- **Keycloak** para autenticación OAuth2/OIDC
- **Flyway** para migraciones de base de datos
- **Docker** y Docker Compose para contenedores
- **Arquitectura Hexagonal** (Ports & Adapters)

## 🏗️ Arquitectura

El proyecto sigue arquitectura hexagonal con clara separación de capas:

- **Domain**: Lógica de negocio, modelos y servicios de dominio
- **Application**: Casos de uso y puertos (interfaces)
- **Infrastructure**: Adaptadores (web controllers, repositorios, configuración)

## 📦 Entorno Local

El entorno local usa tres contenedores: backend, PostgreSQL y Keycloak.

### Requisitos Previos

- Docker Desktop instalado y en ejecución
- Java 21 (opcional, para desarrollo sin Docker)
- Maven (opcional, para desarrollo sin Docker)

### Configuración

1. Copia `.env.example` a `.env` y configura las contraseñas:

```bash
cp .env.example .env
```

2. Levanta el entorno con Docker Compose:

```powershell
docker compose up --build
```

### URLs de Acceso

- **API Backend**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Keycloak Admin**: `http://localhost:8081`
- **PostgreSQL**: `localhost:5433`
- **Debug remoto**: `localhost:5005`

### Usuario de Desarrollo

Usuario precargado para pruebas (roles ADMIN y USER):

- **Usuario**: `admin.dev`
- **Contraseña**: `AdminDev2026!`

⚠️ **Solo para desarrollo local**

## 🧪 Probar la API

1. Importa `postman/NutriGO.postman_collection.json` en Postman
2. Ejecuta `Auth > Obtener token` con las credenciales de desarrollo
3. El token se configurará automáticamente para las demás peticiones

## 🗄️ Base de Datos

Las migraciones de Flyway se ejecutan automáticamente al iniciar:

- `V1__baseline.sql` - Esquema base
- `V2__usuarios.sql` - Tabla de usuarios
- `V3__registros_progreso.sql` - Tabla de progreso
- `V4__planes_y_reportes.sql` - Tablas de planes y reportes

## 🧹 Detener el Entorno

```powershell
# Detener sin borrar datos
docker compose down

# Detener y eliminar volúmenes (limpieza completa)
docker compose down -v
```

## 📚 Documentación Adicional

La planificación y fases de desarrollo están en [docs/RUTA_DESARROLLO.md](docs/RUTA_DESARROLLO.md).

## 👨‍💻 Autor

**Julian Villamizar**
- Email: julian.villamizar200@gmail.com
- GitHub: [@JulianZAR](https://github.com/JulianZAR)

## 📝 Licencia

Proyecto académico - Universidad
