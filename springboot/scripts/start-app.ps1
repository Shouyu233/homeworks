Param(
  [int]$Port = 8080
)

$ErrorActionPreference = 'SilentlyContinue'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$jar = Join-Path $repoRoot 'target/springboot-demo-0.0.1-SNAPSHOT.jar'

if (!(Test-Path $jar)) {
  Write-Error "Jar not found: $jar. Build first: mvn -DskipTests package"
  exit 1
}

function Wait-Alive([int]$port) {
  for ($i=0; $i -lt 30; $i++) {
    try {
      $r = Invoke-WebRequest -Uri "http://localhost:$port/api/hello?name=Spring" -UseBasicParsing -TimeoutSec 2
      if ($r.StatusCode -eq 200) { return $true }
    } catch {}
    Start-Sleep -Milliseconds 500
  }
  return $false
}

$p = Start-Process -FilePath 'java' -ArgumentList @('-jar', $jar, "--server.port=$Port") -WorkingDirectory $repoRoot -PassThru
if (Wait-Alive $Port) {
  Write-Output "RUNNING:$Port:PID=$($p.Id)"
  exit 0
}

try { Stop-Process -Id $p.Id -Force } catch {}

$Port2 = 8081
$p = Start-Process -FilePath 'java' -ArgumentList @('-jar', $jar, "--server.port=$Port2") -WorkingDirectory $repoRoot -PassThru
if (Wait-Alive $Port2) {
  Write-Output "RUNNING:$Port2:PID=$($p.Id)"
  exit 0
}

Write-Error "FAILED to start on ports $Port and $Port2"
exit 1

