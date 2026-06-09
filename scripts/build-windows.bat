@echo off
REM Build Resourcelyi JAR with embedded web dashboard for native Windows run.
setlocal

cd /d "%~dp0\.."

echo Building React dashboard...
cd web
call npm ci
if errorlevel 1 exit /b 1
call npm run build
if errorlevel 1 exit /b 1

echo Copying dashboard into backend...
cd ..\backend
if exist src\main\resources\static rmdir /s /q src\main\resources\static
mkdir src\main\resources\static
xcopy /e /i /y ..\web\dist\* src\main\resources\static\

echo Building Spring Boot JAR...
call mvn package -DskipTests
if errorlevel 1 exit /b 1

echo.
echo Done: backend\target\resourcelyi-backend-3.3.0.jar
echo Run: scripts\run-windows.bat
