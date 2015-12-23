#!/bin/bash
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
echo "********SHUTDOWN TOMCAT********"
cd ${webTomcatBinPath}
./shutdown.sh
cd ${dataTomcatBinPath}
./shutdown.sh
echo "********UNDEPLOYING THE APPLICATION********"
cd ${dataWebappsPath}
rm -rf ROOT ROOT.war
cd ${webWebappsPath}
rm -rf ROOT ROOT.war
echo "********INSERT THE TEST DATABASE********"
cd ${scriptDir}
`mysql -u${mysqlUser} -p${mysqlPassword} ifs < testDataDump.sql`
echo "********BUILD AND DEPLOY THE APPLICATION********"
cd ${dataServiceCodeDir}
./gradlew clean client clientCopy testCommonCode testCommonCodeCopy deployToTomcat
cd ${webServiceCodeDir}
./gradlew clean deployToTomcat
echo "********START THE DATA SERVER********"
cd ${dataTomcatBinPath}
./startup.sh
echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
touch ${dataLogFilePath}
tail -f -n0 ${dataLogFilePath} | while read logLine
do
  [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
done
echo "********START THE WEB SERVER********"
touch ${webTomcatBinPath}
cd ${webTomcatBinPath}
./startup.sh
echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
tail -f -n0 ${webLogFilePath} | while read logLine
do
  [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
done
echo "**********RUN THE WEB TESTS**********"
cd ${scriptDir}
pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase IFS_acceptance_tests/tests/*








