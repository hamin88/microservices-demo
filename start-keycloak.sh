#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
keycloak_home="$repo_root/keycloak-26.6.1"
realm_import="$keycloak_home/data/import"
key_store="$keycloak_home/conf/keycloak-localhost.p12"

if [[ ! -d "$keycloak_home" ]]; then
  echo "Keycloak was not found at $keycloak_home" >&2
  exit 1
fi

find_command() {
  local command_name="$1"
  shift

  if command -v "$command_name" >/dev/null 2>&1; then
    command -v "$command_name"
    return 0
  fi

  for candidate in "$@"; do
    if [[ -x "$candidate" ]]; then
      printf '%s\n' "$candidate"
      return 0
    fi
  done

  return 1
}

psql_bin="$(find_command psql \
  "/c/Program Files/PostgreSQL/18/bin/psql.exe" \
  "/mnt/c/Program Files/PostgreSQL/18/bin/psql.exe" || true)"
createdb_bin="$(find_command createdb \
  "/c/Program Files/PostgreSQL/18/bin/createdb.exe" \
  "/mnt/c/Program Files/PostgreSQL/18/bin/createdb.exe" || true)"

if [[ -n "$psql_bin" && -n "$createdb_bin" ]]; then
  export PGPASSWORD="postgres"
  database_exists="$("$psql_bin" -h localhost -p 5432 -U postgres -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'keycloak';" | tr -d '[:space:]')"
  if [[ "$database_exists" != "1" ]]; then
    "$createdb_bin" -h localhost -p 5432 -U postgres keycloak
  fi
else
  echo "Warning: PostgreSQL tools were not found. Skipping automatic Keycloak database check." >&2
fi

mkdir -p "$realm_import"
cp -f "$repo_root/docker/keycloak/realm-import/microservices-demo-realm.json" "$realm_import/"

if [[ ! -f "$key_store" ]]; then
  keytool -genkeypair \
    -alias keycloak-localhost \
    -keyalg RSA \
    -keysize 2048 \
    -validity 3650 \
    -keystore "$key_store" \
    -storetype PKCS12 \
    -storepass changeit \
    -keypass changeit \
    -dname "CN=localhost, OU=Development, O=Microservices Demo, L=Local, ST=Local, C=IN" \
    -ext "SAN=dns:localhost,ip:127.0.0.1"
fi

export KC_BOOTSTRAP_ADMIN_USERNAME="admin"
export KC_BOOTSTRAP_ADMIN_PASSWORD="admin"

"$keycloak_home/bin/kc.sh" start-dev --import-realm
