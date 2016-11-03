echo "**This script require the following:"
echo "**A local database called ifs_current_live with username: root and password: password"
echo "**An export from the live database called ifs_current_live.sql"
echo "**Clearing the ifs_current_live database"
cd ../ifs-data-service
./gradlew flywayClean -Dflyway.schemas=ifs_current_live
echo "**Importing the live database into the ifs_current_live"
mysql -uroot -ppassword ifs_current_live < ../baseline/ifs_current_live.sql
echo "**Applying the latest patches to the live database"
./gradlew flywayMigrate -Dflyway.schemas=ifs_current_live -Dflyway.locations="db/migration,db/setup"
echo "**Exporting the minimum data from the live"
echo "**TODO Knowing what needs to be here will require trial and error and we will need to check how it compares to UAT"
cd ../baseline
mysqldump -uroot -ppassword ifs_current_live \
                            --ignore-table=ifs_current_live.schema_version \
                            --no-create-info --extended-insert=false > "generated/db/setup/V0_1_4__Minimum_data.sql"




