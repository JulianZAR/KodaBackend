# Ruta de desarrollo del backend NutriGO

## Propósito

Construir un backend académico mantenible para gestionar usuarios, cálculo de IMC, progreso, planes nutricionales y reportes. La primera entrega funcional será el piloto de Usuario + IMC.

## Arquitectura objetivo

Se usará una arquitectura hexagonal pragmática. Las entidades JPA serán el modelo de dominio para evitar mapeos duplicados. La lógica pura, como el cálculo de IMC, vivirá en servicios de dominio sin dependencias de Spring.

Los adaptadores web recibirán y devolverán DTOs. Los servicios de aplicación dependerán de puertos, nunca directamente de controladores ni de repositorios Spring Data.

```text
HTTP / Swagger -> adaptador web -> puerto de entrada -> servicio de aplicación
                                                     -> servicio de dominio
                                                     -> puerto de salida -> adaptador JPA -> PostgreSQL
```

Keycloak será el proveedor de identidad. Gestionará credenciales, inicio de sesión, tokens y roles. NutriGO almacenará los datos de perfil y salud, vinculados mediante `keycloakUserId`.

## Infraestructura local

El entorno local se ejecutará con Docker Compose y tres servicios:

| Servicio | Responsabilidad | Puerto local |
| --- | --- | --- |
| `backend` | API Spring Boot y validación de JWT | 8080 |
| `keycloak` | Autenticación, roles y emisión de tokens | 8081 |
| `postgres` | Bases `nutrigo` y `keycloak` | 5433 (host) → 5432 (contenedor) |

El backend se desarrollará y depurará desde IntelliJ. Docker Desktop ejecutará los servicios. Swagger, Postman y el panel de Keycloak permitirán revisar su comportamiento.

## Fases

### Fase 0. Preparación del repositorio

1. Inicializar la estructura Git: `main`, `develop` y `feature/arquitectura-hexagonal`.
2. Agregar reglas para excluir secretos, artefactos de compilación y archivos del IDE.
3. Mantener esta ruta de desarrollo como referencia del equipo.

**Resultado:** repositorio organizado y preparado para desarrollo colaborativo.

### Fase 1. Esqueleto hexagonal

1. Crear los paquetes `domain`, `application` e `infrastructure`.
2. Crear las subcarpetas de modelos, excepciones, servicios, puertos y adaptadores.
3. Aún no agregar lógica de negocio.

**Resultado:** estructura de capas verificable.

### Fase 2. Infraestructura ejecutable local

1. Crear el proyecto Maven de Spring Boot.
2. Agregar Dockerfile y Docker Compose para backend, PostgreSQL y Keycloak.
3. Definir variables de entorno y un archivo `.env.example` sin secretos.
4. Crear el realm de desarrollo `nutrigo`, los roles `USER`, `ADMIN` y `NUTRITIONIST`, y un usuario administrador de desarrollo.
5. Configurar Flyway con las bases separadas `nutrigo` y `keycloak`.

**Resultado:** `docker compose up --build` levanta el entorno local.

### Fase 3. Piloto Usuario + IMC

1. Modelar `Usuario` y `Rol` como entidades JPA.
2. Crear `CalculadoraIMC` con validación de peso y estatura positivos y razonables.
3. Definir los puertos de entrada y salida de registro y cálculo de IMC.
4. Implementar los casos de uso y el adaptador JPA.
5. Construir DTOs, controladores y documentación Swagger para el contrato existente de usuarios e IMC.
6. Proteger los endpoints con JWT de Keycloak y aplicar autorización por rol cuando corresponda.
7. Escribir pruebas unitarias de la calculadora y pruebas de los casos de uso con mocks.

**Punto de revisión:** el equipo valida el piloto antes de desarrollar los módulos restantes.

### Fase 4. Progreso

1. Modelar `RegistroProgreso` y sus relaciones.
2. Implementar registro y consulta ordenada del historial.
3. Mantener el IMC asociado a cada punto de progreso.
4. Agregar pruebas, DTOs y documentación para `/api/progress`.

### Fase 5. Planes y reportes

1. Modelar `PlanNutricional` y `Reporte`.
2. Implementar los casos de uso, puertos y adaptadores necesarios.
3. Aplicar permisos para el rol de nutricionista y administrador.
4. Completar los endpoints `/api/reports` y los contratos asociados.

### Fase 6. Seguridad, calidad y entrega

1. Validar el flujo completo de autenticación y autorización con Keycloak.
2. Ejecutar la colección Postman contra el entorno Docker.
3. Documentar OpenAPI, decisiones de arquitectura y variables necesarias.
4. Configurar integración continua: compilación, pruebas JUnit y pruebas Postman.
5. Preparar la estrategia de despliegue después de validar el MVP local.

## Reglas de implementación

- No exponer entidades JPA en respuestas HTTP.
- No poner lógica de negocio en controladores ni repositorios.
- Mantener la compatibilidad del contrato REST existente para `/api/users`, `/api/imc`, `/api/progress` y `/api/reports`.
- No guardar contraseñas en la base de datos de NutriGO.
- Mantener secretos fuera de Git.
- Construir y revisar una fase a la vez.
