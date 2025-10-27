$ErrorActionPreference = 'SilentlyContinue'

$ports = @(8080, 8081)
$usedPort = $null
foreach ($p in $ports) {
  try {
    $r = Invoke-RestMethod -Uri ("http://localhost:{0}/api/hello?name=Spring" -f $p) -TimeoutSec 5
    $usedPort = $p
    break
  } catch {}
}
if (-not $usedPort) { Write-Error 'App not responding'; exit 1 }

function Pay($payload) {
  $json = $payload | ConvertTo-Json -Depth 6
  $url = "http://localhost:{0}/api/pay" -f $usedPort
  return Invoke-RestMethod -Uri $url -Method Post -ContentType 'application/json' -Body $json -TimeoutSec 10
}

$w = Pay(@{ method='WECHAT'; orderId='ORDER-1001'; amount=9.9; currency='CNY'; extra=@{ openId='u-open-id' } })
$a = Pay(@{ method='ALIPAY'; orderId='ORDER-1002'; amount=19.9; currency='CNY'; extra=@{ alipayUserId='2088xxxx' } })
$c = Pay(@{ method='CREDIT_CARD'; orderId='ORDER-1003'; amount=29.9; currency='USD'; extra=@{ cardNo='4111111111111111' } })

$w,$a,$c | ConvertTo-Json -Depth 6

