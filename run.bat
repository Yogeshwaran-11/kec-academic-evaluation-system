@echo off
echo =========================================================================
echo  Starting Kongu Engineering College (R2024) Academic Evaluation System
echo =========================================================================
java -cp "target/classes;lib/mysql-connector-j-8.3.0.jar;." com.kec.MainApp %*
pause
