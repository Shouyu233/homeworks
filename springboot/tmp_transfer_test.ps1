$body = '{"fromUserId":1, "toUserId":2, "amount":100000.00}'
try {
  $resp = Invoke-RestMethod -Uri http://localhost:8080/api/transfer -Method Post -Body $body -ContentType 'application/json' -ErrorAction Stop
  Write-Output "SUCCESS: $resp"
} catch {
  $err = $_.Exception.Response
  if ($err -ne $null) {
    $sr = New-Object System.IO.StreamReader($err.GetResponseStream())
    $txt = $sr.ReadToEnd()
    Write-Output "ERROR-BODY: $txt"
  } else {
    Write-Output "ERROR: $_"
  }
}
