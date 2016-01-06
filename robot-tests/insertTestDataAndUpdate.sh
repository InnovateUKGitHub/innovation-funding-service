#!/bin/bash
cd "$(dirname "$0")"
echo "********GETTING ALL THE VARIABLES********"
scriptDir=`pwd`
echo "scriptDir:        ${scriptDir}"
dateFormat=`date +%Y-%m-%d`
cd ../ifs-data-service
dataServiceCodeDir=`pwd`
echo "dataServiceCodeDir:${dataServiceCodeDir}"
mysqlUser=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsDatasourceUsername'  | cut -d "=" -f2 | sed 's/"//g'`
echo "mysqlUser:         ${mysqlUser}"
mysqlPassword=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsDatasourcePassword'  | cut -d "=" -f2 | sed 's/"//g'`
echo "********INSERT THE TEST DATABASE********"
cd ${scriptDir}
`mysql -u${mysqlUser} -p${mysqlPassword} ifs < testDataDump.sql`
cd ${dataServiceCodeDir}
./gradlew flywayMigrate








