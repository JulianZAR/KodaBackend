FROM quay.io/keycloak/keycloak:26.0
COPY themes /opt/keycloak/themes
COPY nutrigo-realm.json /opt/keycloak/data/import/nutrigo-realm.json
RUN /opt/keycloak/bin/kc.sh build --db=postgres
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized", "--import-realm", "--http-enabled=true", "--hostname-strict=false", "--proxy-headers=xforwarded"]
