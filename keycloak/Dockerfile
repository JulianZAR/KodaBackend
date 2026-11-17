FROM quay.io/keycloak/keycloak:26.0

# Opciones de build (Quarkus las fija en la imagen optimizada)
# KC_CACHE=local: una sola instancia, sin cluster Infinispan/JGroups (ahorra RAM e hilos)
ENV KC_DB=postgres \
    KC_HEALTH_ENABLED=true \
    KC_CACHE=local

COPY themes /opt/keycloak/themes
# Realm: solo se importa si NO existe todavía; cambios posteriores se hacen
# desde la consola de administración.
COPY nutrigo-realm.json /opt/keycloak/data/import/nutrigo-realm.json
RUN /opt/keycloak/bin/kc.sh build

# Opciones de runtime pensadas para Render Free (0.1 CPU / 512 MB):
# - Pool de BD pequeño para no agotar conexiones ni esperar en el arranque.
# - Heap fijo (por defecto Keycloak usa el 70% de la RAM y el proceso total
#   supera 512 MB -> Render lo mata por "Ran out of memory").
# - Límites de metaspace, code cache, memoria directa (Netty) y pila de hilos;
#   pocos hilos HTTP.
# - Solo compilador C1: arranque más rápido y menos memoria (Keycloak ya fija G1GC).
# Se pueden sobrescribir con variables de entorno en Render.
ENV KC_DB_POOL_INITIAL_SIZE=1 \
    KC_DB_POOL_MIN_SIZE=1 \
    KC_DB_POOL_MAX_SIZE=5 \
    KC_HTTP_POOL_MAX_THREADS=10 \
    JAVA_OPTS_KC_HEAP="-Xms64m -Xmx224m" \
    JAVA_OPTS_APPEND="-XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=48m -XX:MaxDirectMemorySize=48m -Xss512k -XX:TieredStopAtLevel=1"

EXPOSE 8080
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized", "--import-realm", "--http-enabled=true", "--hostname-strict=false", "--proxy-headers=xforwarded"]
