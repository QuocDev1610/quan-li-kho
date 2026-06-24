@echo off
REM Script để build và chạy ứng dụng Spring Boot

echo ========================================
echo Building Project...
echo ========================================

REM Kill existing Java processes
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 >nul

REM Build project
call mvnw.cmd clean package -DskipTests

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Starting Application...
echo ========================================
echo URL: http://localhost:8080/dashboard
echo.
echo Press Ctrl + C to stop the application
echo ========================================
echo.

REM Run the jar
java -jar target/Project-1.0-SNAPSHOT.war

pause

