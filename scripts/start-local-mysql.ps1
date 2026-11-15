param(
    [int]$Port = 3307
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$mysqlHome = 'C:\Program Files\MySQL\MySQL Server 8.0'
$mysqld = Join-Path $mysqlHome 'bin\mysqld.exe'
$mysql = Join-Path $mysqlHome 'bin\mysql.exe'
$dataDir = Join-Path $projectRoot '.local\mysql-data'
$logFile = Join-Path $projectRoot '.local\mysql.log'
$pidFile = Join-Path $projectRoot '.local\mysql.pid'
$sqlFile = Join-Path $projectRoot 'database\01_create_database.sql'

if (-not (Test-Path $mysqld)) {
    throw "No se encontro mysqld.exe en '$mysqld'. Ajusta la ruta en este script."
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $dataDir) | Out-Null

function Test-Port {
    param([int]$TargetPort)
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $client.Connect('127.0.0.1', $TargetPort)
        $client.Close()
        return $true
    } catch {
        return $false
    }
}

function Test-MySqlReady {
    & $mysql --protocol=tcp -h 127.0.0.1 -P $Port -u root -e "SELECT 1" 2>$null | Out-Null
    return $LASTEXITCODE -eq 0
}

if (-not (Test-Path (Join-Path $dataDir 'mysql'))) {
    Write-Host "Inicializando una instancia local de MySQL en $dataDir ..."
    New-Item -ItemType Directory -Force -Path $dataDir | Out-Null
    & $mysqld --no-defaults --initialize-insecure "--basedir=$mysqlHome" "--datadir=$dataDir" "--log-error=$logFile"
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo inicializar MySQL. Revisa $logFile"
    }
}

if (-not (Test-Port $Port)) {
    Write-Host "Arrancando MySQL en 127.0.0.1:$Port ..."
    $arguments = @(
        '--no-defaults',
        "--basedir=`"$mysqlHome`"",
        "--datadir=`"$dataDir`"",
        "--port=$Port",
        '--bind-address=127.0.0.1',
        "--log-error=`"$logFile`"",
        "--pid-file=`"$pidFile`""
    )
    Start-Process -FilePath $mysqld -ArgumentList $arguments -WindowStyle Hidden | Out-Null

    $ready = $false
    for ($attempt = 1; $attempt -le 60; $attempt++) {
        Start-Sleep -Seconds 1
        if (Test-MySqlReady) {
            $ready = $true
            break
        }
    }

    if (-not $ready) {
        throw "MySQL no respondio en el puerto $Port. Revisa $logFile"
    }
} else {
    Write-Host "El puerto $Port ya esta en uso; se reutilizara la instancia activa."
}

Write-Host "Creando base de datos y usuario de aplicacion ..."
$sql = Get-Content -Raw $sqlFile
& $mysql --protocol=tcp -h 127.0.0.1 -P $Port -u root -e $sql
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo preparar la base de datos barberturn."
}

Write-Host ''
Write-Host "MySQL listo en 127.0.0.1:$Port"
Write-Host 'Base de datos: barberturn'
Write-Host 'Usuario: barberturn / Contrasena: barberturn'
