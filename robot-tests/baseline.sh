#!/bin/bash
# Copy the old baseline into the database
mysql -uifs -pifs ifs < testDataDump.sql
# Go to the ifs-data-service 
cd ../../innovation-funding-service/ifs-data-service/
# Run any migrations on the database
./gradlew flywayMigrate
# Go back to the database dump directory
cd -
# Export the new database
mysqldump --add-drop-table --extended-insert=false ifs -uifs -pifs > testDataDump.sql





