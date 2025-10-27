function post($obj){
  $json = $obj | ConvertTo-Json
  try{
    $r = Invoke-RestMethod -Uri http://localhost:8080/api/transfer -Method Post -Body $json -ContentType 'application/json' -ErrorAction Stop
    Write-Output ("OK: " + ($r | ConvertTo-Json -Depth 5))
  } catch {
    $resp = $_.Exception.Response
    if ($resp -ne $null) {
      $sr = New-Object System.IO.StreamReader($resp.GetResponseStream())
      $txt = $sr.ReadToEnd()
      Write-Output ("ERR: " + $txt)
    } else { Write-Output ("ERR: " + $_) }
  }
}

post @{ fromUserId=999; toUserId=2; amount=10.00 }
post @{ fromUserId=1; toUserId=1; amount=10.00 }
post @{ fromUserId=1; toUserId=2; amount=10.00 }
