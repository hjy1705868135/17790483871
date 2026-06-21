@echo off
set "JAVA_HOME=C:\Users\69394\.jdks\openjdk-25.0.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo ==========================================
echo  电商购物平台 - 后端服务启动
echo ==========================================
echo JAVA_HOME: %JAVA_HOME%
echo Port: 8081
echo ==========================================
cd /d "%~dp0baseplatform"
call mvnw.cmd spring-boot:run -DskipTests
pause
