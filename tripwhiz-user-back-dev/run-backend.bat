@echo off
cd /d "%~dp0"
set PORT_PID=
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8081" ^| findstr "LISTENING"') do set PORT_PID=%%a

if defined PORT_PID (
    echo TripWhiz backend port 8081 is already in use by PID %PORT_PID%.
    echo If http://localhost:8081/ returns OK, the backend is already running.
    echo To restart it, run stop-backend.bat first.
    exit /b 0
)

call gradlew.bat bootRun
