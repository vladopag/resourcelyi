@echo off
REM Run Resourcelyi natively on Windows (full host metrics).
REM Requires JDK 21+: https://adoptium.net/
REM Build first: cd backend && mvn package -DskipTests

set JAR=..\backend\target\resourcelyi-backend-3.2.0.jar
if not exist "%JAR%" (
  echo JAR not found. Build with: cd backend ^&^& mvn package -DskipTests
  exit /b 1
)

java -jar "%JAR%" --resourcelyi.disk-path=C:\
