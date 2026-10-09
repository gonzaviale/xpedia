$ErrorActionPreference = 'Stop'
$backendDir = Split-Path -Parent $PSScriptRoot
$seedPath = Join-Path $backendDir 'src/main/resources/db/dev/ruta-piloto.sql'
$composePath = Join-Path $backendDir 'docker-compose.yml'

if (-not (Test-Path -LiteralPath $seedPath)) { throw 'No se encontró el archivo de datos piloto.' }

# Destino explícito: PostgreSQL local del compose de este backend.
# UTF-8 mantiene títulos y palabras clave al enviarlos a psql por stdin.
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
Get-Content -LiteralPath $seedPath -Raw -Encoding UTF8 |
    docker compose -f $composePath exec -T postgres psql -U xpedia_user -d xpedia -v ON_ERROR_STOP=1
if ($LASTEXITCODE -ne 0) { throw 'No se pudo cargar el piloto. Revisá el error de PostgreSQL; la carga usa una transacción.' }
Write-Host 'Piloto cargado. Ruta: b1000000-0000-4000-8000-000000000001'
