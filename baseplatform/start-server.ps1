# 无人机信息管理系统启动脚本
# 请在PowerShell中运行此脚本

# 设置工作目录
Set-Location "c:\Users\69394\Desktop\cupro\baseplatform"

# 使用Maven启动Spring Boot应用
Write-Host "正在启动无人机信息管理系统..." -ForegroundColor Cyan
Write-Host "端口: 8086" -ForegroundColor Yellow
Write-Host ""

# 启动命令
.\mvnw.cmd spring-boot:run