param(
    [int]$Port = 3307
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$mysqlHome = 'C:\Program Files\MySQL\MySQL Server 8.0'
$mysqladmin = Join-Path $mysqlHome 'bin\mysqladmin.exe'
$pidFile = Join-Path $projectRoot '.local\mysql.pid'

if (-not (Test-Path $mysqladmin)) {
    throw "No se encontro mysqladmin.exe en '$mysqladmin'."
}

try {
    $client = New-Object System.Net.Sockets.TcpClient
    $client.Connect('127.0.0.1', $Port)
    $client.Close()
} catch {
    Write-Host "MySQL no esta escuchando en el puerto $Port."
    exit 0
}

& $mysqladmin --protocol=tcp -h 127.0.0.1 -P $Port -u root shutdown
if ($LASTEXITCODE -eq 0) {
    Write-Host "MySQL detenido en el puerto $Port."
    exit 0
}

if (Test-Path $pidFile) {
    $processId = Get-Content $pidFile | Select-Object -First 1
    Stop-Process -Id $processId -Force
    Write-Host "MySQL detenido usando el archivo de PID $pidFile."
    exit 0
}

throw "No se pudo detener MySQL en el puerto $Port."
