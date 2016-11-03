echo "Copy the read me files to the generated directories"
cp ../ifs-data-service/src/main/resources/db/migration/SCRIPTS_THAT_WILL_BE_RUN_ON_ALL_ENVIRONMENTS_INCLUDING_PRODUCTION generated/db/migration/SCRIPTS_THAT_WILL_BE_RUN_ON_ALL_ENVIRONMENTS_INCLUDING_PRODUCTION
cp ../ifs-data-service/src/main/resources/db/setup/SETUP_THAT_WILL_BE_RUN_ON_PRODUCTION generated/db/setup/SETUP_THAT_WILL_BE_RUN_ON_PRODUCTION
cp ../ifs-data-service/src/main/resources/db/webtest/SETUP_THAT_WILL_BE_RUN_FOR_ACCEPTANCE_TESTS generated/db/webtest/SETUP_THAT_WILL_BE_RUN_FOR_ACCEPTANCE_TESTS
echo "Deleting the old scripts and directories"
rm -rf ../ifs-data-service/src/main/resources/db
echo "Moving the generated files into the code base"
rsync -r --exclude=.gitignore generated/db ../ifs-data-service/src/main/resources/ 
