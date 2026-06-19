@echo off
set PORT_PID=
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8081" ^| findstr "LISTENING"') do set PORT_PID=%%a

if not defined PORT_PID (
    echo No process is listening on port 8081.
    exit /b 0
)

echo Stopping process on port 8081, PID %PORT_PID%...
taskkill /PID %PORT_PID% /F
