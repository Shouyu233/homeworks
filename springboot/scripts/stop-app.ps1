$ErrorActionPreference = 'SilentlyContinue'
$procs = Get-CimInstance Win32_Process -Filter "Name='java.exe'"
foreach ($p in $procs) {
  if ($p.CommandLine -and $p.CommandLine -like '*springboot-demo-0.0.1-SNAPSHOT.jar*') {
    try { Stop-Process -Id $p.ProcessId -Force; Write-Output ("Stopped PID {0}" -f $p.ProcessId) } catch {}
  }
}

