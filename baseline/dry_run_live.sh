echo "**This script requires / does the following:"
echo "**A local database with username: root and password: password"
echo "**An export from the live database called ifs_current_live.sql"
echo "Creating the database if it does not exist"
echo "CREATE SCHEMA IF NOT EXISTS ifs_dry_run_live" | mysql -uroot -ppassword
cd ../ifs-data-service
./gradlew flywayClean -Dflyway.schemas=ifs_dry_run_live
echo "**Importing the live database into the ifs_dry_run_live"
mysql -uroot -ppassword ifs_dry_run_live < ../baseline/ifs_current_live.sql
echo "**Replacing the schema_version table"
mysql -uroot -ppassword ifs_dry_run_live < ../baseline/generated/schema_version_statement_for_uat_and_live.sql
echo "**Applying the latest patches to the live database"
./gradlew flywayMigrate -Dflyway.schemas=ifs_dry_run_live -Dflyway.locations="db/migration,db/setup"
cd ../baseline
