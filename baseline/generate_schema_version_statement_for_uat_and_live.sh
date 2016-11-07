echo "**This script requires / does the following:"
echo "**A local database with username: root and password: password"
echo "Creating the database if it does not exist"
echo "CREATE SCHEMA IF NOT EXISTS Clearing the ifs_database_to_generate_schema_version" | mysql -uroot -ppassword 
echo "**Clearing the ifs_database_to_generate_schema_version database"
cd ../ifs-data-service
./gradlew flywayClean -Dflyway.schemas=ifs_database_to_generate_schema_version
echo "**Running the database scripts - this requires that the new ones have been copied across to the code"
./gradlew flywayMigrate -Dflyway.schemas=ifs_database_to_generate_schema_version -Dflyway.locations="db/migration,db/setup"
echo "**Exporting the schema_version table"
cd ../baseline
mysqldump -uroot -ppassword ifs_database_to_generate_schema_version \
                            schema_version \
			    --extended-insert=false > "generated/schema_version_statement_for_uat_and_live.sql"



