# =============================================================================
# NutriGO Backend — Dockerfile (Multi-stage build)
# =============================================================================
# Patron: Multi-stage build
#   - Etapa "build": Compila el proyecto con Maven sin instalar Maven en la
#     imagen final, reduciendo el tamano y la superficie de ataque.
#   - Etapa final: Solo contiene el JRE y el JAR ejecutable.
#
# Tecnologias: Java 21, Maven 3.9, Spring Boot 3.5
# =============================================================================

# -----------------------------------------------------------------------------
# Etapa 1: BUILD — Compilar el proyecto con Maven
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /workspace

# Copiar el POM primero para aprovechar el cache de capas de Docker:
# Si el pom.xml no cambia, Docker reutiliza la capa de dependencias
# y no las descarga de nuevo en cada build.
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copiar el codigo fuente y compilar el JAR
COPY src ./src
RUN mvn -B -ntp package -DskipTests

# -----------------------------------------------------------------------------
# Etapa 2: RUNTIME — Imagen final liviana solo con el JRE
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre

# Metadatos de la imagen (buena practica OCI)
LABEL maintainer="Equipo NutriGO"
LABEL description="Backend API REST de NutriGO - Spring Boot 3.5 / Java 21"
LABEL version="0.0.1-SNAPSHOT"

# Directorio de trabajo de la aplicacion
WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
# El wildcard permite que funcione independientemente de la version exacta
COPY --from=build /workspace/target/nutrigo-backend-*.jar app.jar

# Puerto 8080: API REST de Spring Boot
# Puerto 5005: Debug remoto JDWP (solo desarrollo)
EXPOSE 8080 5005

# Comando de inicio: ejecutar el JAR de Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
