$ErrorActionPreference = 'Stop'

mvn -q -DskipTests package | Out-Null

$p = Start-Process -PassThru -WindowStyle Hidden -FilePath java -ArgumentList '-jar','target/springboot-demo-0.0.1-SNAPSHOT.jar'

$ready = $false
for ($i=0; $i -lt 60; $i++) {
  try {
    Invoke-RestMethod -Uri http://localhost:8080/api/hello?name=Spring -TimeoutSec 2 | Out-Null
    $ready = $true
    break
  } catch {
    Start-Sleep -Milliseconds 500
  }
}
if (-not $ready) {
  Write-Output 'ERR: server not ready'
  Stop-Process -Id $p.Id -Force
  exit 1
}

function Test-Backend($backend) {
  Write-Output ("=== TEST backend=" + $backend + " ===")
  $q = "?backend=$backend"
  Write-Output 'LIST1:'
  $list1 = Invoke-RestMethod -Uri ("http://localhost:8080/api/users$q") -TimeoutSec 10
  $list1 | ConvertTo-Json -Depth 5

  Write-Output 'CREATED:'
  $created = Invoke-RestMethod -Method Post -Uri ("http://localhost:8080/api/users$q") -ContentType 'application/json' -Body (@{name='ZhangSan'; age=30; email='zhangsan@example.com'} | ConvertTo-Json)
  $created | ConvertTo-Json -Depth 5

  $cid = $created.id

  Write-Output 'UPDATED:'
  $updated = Invoke-RestMethod -Method Put -Uri ("http://localhost:8080/api/users/$cid$q") -ContentType 'application/json' -Body (@{name='LiSi'; age=$null; email='lisi@example.com'} | ConvertTo-Json)
  $updated | ConvertTo-Json -Depth 5

  Write-Output 'GET_AFTER_UPDATE:'
  $get = Invoke-RestMethod -Uri ("http://localhost:8080/api/users/$cid$q")
  $get | ConvertTo-Json -Depth 5

  try {
    Invoke-RestMethod -Method Delete -Uri ("http://localhost:8080/api/users/$cid$q") | Out-Null
    Write-Output 'DELETED_STATUS:204'
  } catch {
    Write-Output ('DELETED_STATUS_ERR:' + $_.Exception.Message)
  }

  try {
    Invoke-RestMethod -Uri ("http://localhost:8080/api/users/$cid$q") -TimeoutSec 5 | Out-Null
    Write-Output 'GET_AFTER_DELETE_STATUS:unexpected-200'
  } catch {
    $code = 0
    try { $code = [int]$_.Exception.Response.StatusCode } catch {}
    Write-Output ("GET_AFTER_DELETE_STATUS:" + $code)
  }
}

Test-Backend 'jt'
Test-Backend 'raw'

Write-Output 'PAY_WECHAT:'
$pay = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/pay -ContentType 'application/json' -Body (@{method='WECHAT'; orderId='ORDER-1001'; amount=88.88; currency='CNY'; extra=@{openId='openid-123'}} | ConvertTo-Json -Depth 5)
$pay | ConvertTo-Json -Depth 6

Write-Output 'ECHO:'
$echo = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/echo -ContentType 'application/json' -Body (@{name='Alice'; age=23; email='alice@example.com'} | ConvertTo-Json)
$echo | ConvertTo-Json -Depth 5

try {
  if ($p -and (Get-Process -Id $p.Id -ErrorAction SilentlyContinue)) {
    Stop-Process -Id $p.Id -Force
  }
} catch {}
