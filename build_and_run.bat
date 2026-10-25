@echo off
echo =========================================================================
echo  Compiling Kongu Engineering College (R2024) Academic Evaluation System
echo =========================================================================
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo.
echo =========================================================================
echo  Launching System Menu
echo =========================================================================
java -cp "target/classes;lib/mysql-connector-j-8.3.0.jar;." com.kec.MainApp
pause
