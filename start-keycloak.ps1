$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$keycloakHome = Join-Path $repoRoot "keycloak-26.6.1"
$realmImport = Join-Path $keycloakHome "data\import"
$keyStore = Join-Path $keycloakHome "conf\keycloak-localhost.p12"
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
$createdb = "C:\Program Files\PostgreSQL\18\bin\createdb.exe"

if (-not (Test-Path $keycloakHome)) {
    throw "Keycloak was not found at $keycloakHome"
}

if ((Test-Path $psql) -and (Test-Path $createdb)) {
    $env:PGPASSWORD = "postgres"
    $databaseExists = & $psql -h localhost -p 5432 -U postgres -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'keycloak';"
    if ($databaseExists.Trim() -ne "1") {
        & $createdb -h localhost -p 5432 -U postgres keycloak
    }
} else {
    Write-Warning "PostgreSQL tools were not found. Skipping automatic Keycloak database check."
}

New-Item -ItemType Directory -Force -Path $realmImport | Out-Null
Copy-Item -Force `
    -Path (Join-Path $repoRoot "docker\keycloak\realm-import\microservices-demo-realm.json") `
    -Destination $realmImport

if (-not (Test-Path $keyStore)) {
    & keytool -genkeypair `
        -alias keycloak-localhost `
        -keyalg RSA `
        -keysize 2048 `
        -validity 3650 `
        -keystore $keyStore `
        -storetype PKCS12 `
        -storepass changeit `
        -keypass changeit `
        -dname "CN=localhost, OU=Development, O=Microservices Demo, L=Local, ST=Local, C=IN" `
        -ext "SAN=dns:localhost,ip:127.0.0.1"
}

$env:KC_BOOTSTRAP_ADMIN_USERNAME = "admin"
$env:KC_BOOTSTRAP_ADMIN_PASSWORD = "admin"

& (Join-Path $keycloakHome "bin\kc.bat") start-dev --import-realm
