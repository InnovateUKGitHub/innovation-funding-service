@ECHO OFF
"C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe" -uroot -p ifs < testDataDump.sql
cd ..\ifs-data-service\
gradlew.bat  flywayMigrate -Pflyway.validateOnMigrate=false


