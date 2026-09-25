#!/usr/bin/env bash
set -euo pipefail

psql --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
  --set=nutrigo_db_name="$NUTRIGO_DB_NAME" \
  --set=nutrigo_db_user="$NUTRIGO_DB_USER" \
  --set=nutrigo_db_password="$NUTRIGO_DB_PASSWORD" \
  --set=keycloak_db_name="$KEYCLOAK_DB_NAME" \
  --set=keycloak_db_user="$KEYCLOAK_DB_USER" \
  --set=keycloak_db_password="$KEYCLOAK_DB_PASSWORD" <<'EOSQL'
CREATE ROLE :"nutrigo_db_user" LOGIN PASSWORD :'nutrigo_db_password';
CREATE DATABASE :"nutrigo_db_name" OWNER :"nutrigo_db_user";

CREATE ROLE :"keycloak_db_user" LOGIN PASSWORD :'keycloak_db_password';
CREATE DATABASE :"keycloak_db_name" OWNER :"keycloak_db_user";
EOSQL
