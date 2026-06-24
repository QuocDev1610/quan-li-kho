#!/usr/bin/env pwsh
# Script để build và chạy ứng dụng Spring Boot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Project..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

# Stop existing Java processes
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Build project
.\mvnw.ps1 clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Starting Application..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "URL: http://localhost:8080/dashboard" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Run the jar
java -jar target/Project-1.0-SNAPSHOT.war

# Keep console open if jar exits
Read-Host "Press any key to close..."

