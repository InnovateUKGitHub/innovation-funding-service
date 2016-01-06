#!/bin/bash


testDirectory='IFS_acceptance_tests/tests/*'
if [ -n "$1" ]; then
 testDirectory="$1"
fi

cd "$(dirname "$0")"
echo "********GETTING ALL THE VARIABLES********"
scriptDir=`pwd`
echo "scriptDir:        ${scriptDir}"
dateFormat=`date +%Y-%m-%d`
cd ../ifs-data-service
dataServiceCodeDir=`pwd`
echo "dataServiceCodeDir:${dataServiceCodeDir}"
dataWebappsPath=`sed '/^\#/d' dev-build.gradle | grep 'ext.tomcatWebAppsDir'  | cut -d "=" -f2 | sed 's/"//g'`
echo "dataWebappsPath:   ${dataWebappsPath}"
dataTomcatPath="$(dirname "$dataWebappsPath")"
echo "dataTomcatPath:    ${dataTomcatPath}"
dataTomcatBinPath=${dataTomcatPath}"/bin"
echo "dataTomcatBinPath:    ${dataTomcatBinPath}"
dataLogPath=$dataTomcatPath"/logs"
echo "dataLogPath:       ${dataLogPath}"
dataLogFilePath=${dataLogPath}"/catalina."${dateFormat}".log"
echo "dataLogFilePath:   ${dataLogFilePath}"
dataPort=`sed '/^\#/d' dev-build.gradle | grep 'ext.serverPort'  | cut -d "=" -f2 | sed 's/"//g'`
echo "dataPort:          ${dataPort}"
mysqlUser=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsDatasourceUsername'  | cut -d "=" -f2 | sed 's/"//g'`
echo "mysqlUser:         ${mysqlUser}"
mysqlPassword=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsDatasourcePassword'  | cut -d "=" -f2 | sed 's/"//g'`
cd ../ifs-web-service
webServiceCodeDir=`pwd`
echo "webServiceCodeDir: ${webServiceCodeDir}"
webWebappsPath=`sed '/^\#/d' dev-build.gradle | grep 'ext.tomcatWebAppsDir'  | cut -d "=" -f2 | sed 's/"//g'`
echo "webWebappsPath:    ${webWebappsPath}"
webTomcatPath="$(dirname "$webWebappsPath")"
echo "webTomcatPath:     ${webTomcatPath}"
webTomcatBinPath=${webTomcatPath}"/bin"
echo "webTomcatBinPath:     ${webTomcatBinPath}"
webLogPath=$webTomcatPath"/logs"
echo "webLogPath:        ${webLogPath}"
webLogFilePath=${webLogPath}"/catalina."${dateFormat}".log"
echo "webLogFilePath:    ${webLogFilePath}"
webPort=`sed '/^\#/d' dev-build.gradle | grep 'ext.serverPort'  | cut -d "=" -f2 | sed 's/"//g'`
echo "webPort:           ${webPort}"
webBase="localhost:"${webPort}
echo "webBase:           ${webBase}"

