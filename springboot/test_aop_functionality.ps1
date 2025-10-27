# AOP功能测试脚本
Write-Host "测试AOP切面功能..." -ForegroundColor Green

# 等待应用完全启动
Start-Sleep -Seconds 3

# 测试1: 调用普通Controller方法
Write-Host "`n1. 测试普通Controller方法..." -ForegroundColor Yellow
$response1 = Invoke-RestMethod -Uri "http://localhost:8080/api/hello" -Method Get
Write-Host "响应: $($response1 | ConvertTo-Json)"

# 测试2: 测试带参数的Controller方法
Write-Host "`n2. 测试带参数的Controller方法..." -ForegroundColor Yellow
$response2 = Invoke-RestMethod -Uri "http://localhost:8080/api/user/123" -Method Get
Write-Host "响应: $($response2 | ConvertTo-Json)"

# 测试3: 测试POST请求
Write-Host "`n3. 测试POST请求..." -ForegroundColor Yellow
$body = @{
    name = "张三"
    age = 25
    email = "zhangsan@example.com"
} | ConvertTo-Json
$response3 = Invoke-RestMethod -Uri "http://localhost:8080/api/echo" -Method Post -Body $body -ContentType "application/json"
Write-Host "响应: $($response3 | ConvertTo-Json)"

# 测试4: 查看性能统计
Write-Host "`n4. 查看性能统计信息..." -ForegroundColor Yellow
$response4 = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics" -Method Get
Write-Host "性能统计: $($response4 | ConvertTo-Json)"

# 测试5: 查看性能统计概览
Write-Host "`n5. 查看性能统计概览..." -ForegroundColor Yellow
$response5 = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/summary" -Method Get
Write-Host "性能概览: $($response5 | ConvertTo-Json)"

Write-Host "`nAOP功能测试完成！请查看控制台日志确认AOP切面是否正常工作。" -ForegroundColor Green
Write-Host "在应用日志中应该能看到类似以下信息：" -ForegroundColor Cyan
Write-Host "- Controller方法开始执行" -ForegroundColor Cyan
Write-Host "- Controller方法执行完成" -ForegroundColor Cyan
Write-Host "- 方法执行耗时统计" -ForegroundColor Cyan
Write-Host "- 异常处理日志（如果有异常发生）" -ForegroundColor Cyan