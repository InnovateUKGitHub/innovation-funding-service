#!/bin/bash
echo "**This script requires / does the following:"
echo "**A local database with username: root and password: password"
echo "Creating the database if it does not exist"
mysql -uroot -ppassword -e "CREATE SCHEMA IF NOT EXISTS ifs_baseline"
cd ../ifs-data-service
echo "**Building a fresh database running the scripts that have been or will be run on LIVE"
./gradlew flywayClean -Dflyway.schemas=ifs_baseline
./gradlew flywayMigrate -Dflyway.schemas=ifs_baseline -Dflyway.locations="db/migration,db/setup"
cd ../baseline
echo "**Dump the schema and only the schema. This is the first new flyway script in the migration folder"
mysqldump -uroot -ppassword ifs_baseline -d --ignore-table=ifs_baseline.schema_version > "generated/db/migration/V0_1_1__Base_schema.sql"
echo "**Dump the core reference data, which is referenced directly in the code. This is the second new flyway script in the migration folder"
mysqldump -uroot -ppassword ifs_baseline \
                            address_type \
                            application_status \
			    category \
                            form_validator \
			    form_input_type \
                            organisation_type \
                            participant_status \
                            project_role \
                            role \
                            --no-create-info --extended-insert=false > "generated/db/migration/V0_1_2__Reference_data.sql"
echo "**Copy a script to create the system users, note that we do not do this as a dump or reference ids as they are different in UAT and LIVE. This is the third flyway script in the migration folder"
cp V0_1_3__System_users.sql generated/db/migration/V0_1_3__System_users.sql
cd ../baseline





