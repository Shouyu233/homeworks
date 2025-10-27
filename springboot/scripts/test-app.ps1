$ErrorActionPreference = 'SilentlyContinue'

$ports = @(8080, 8081)
$usedPort = $null

foreach ($p in $ports) {
  try {
    $r = Invoke-RestMethod -Uri ("http://localhost:{0}/api/hello?name=Spring" -f $p) -TimeoutSec 5
    Write-Output ("GET_OK:{0}" -f $p)
    $usedPort = $p
    break
  } catch {}
}

if (-not $usedPort) {
  Write-Error "NO_PORT_OK"
  exit 1
}

$bodyObj = [pscustomobject]@{ name = 'Alice'; age = 20; email = 'alice@example.com' }
$json = $bodyObj | ConvertTo-Json -Depth 5
$resp = Invoke-RestMethod -Uri ("http://localhost:{0}/api/echo" -f $usedPort) -Method Post -ContentType 'application/json' -Body $json -TimeoutSec 5
Write-Output ("POST_OK:{0}" -f $usedPort)
$resp | ConvertTo-Json -Depth 5

