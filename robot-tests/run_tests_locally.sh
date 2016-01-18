#!/bin/bash

function coloredEcho(){
    local exp=$1;
    local color=$2;
    if ! [[ $color =~ '^[0-9]$' ]] ; then
       case $(echo $color | tr '[:upper:]' '[:lower:]') in
        black) color=0 ;;
        red) color=1 ;;
        green) color=2 ;;
        yellow) color=3 ;;
        blue) color=4 ;;
        magenta) color=5 ;;
        cyan) color=6 ;;
        white|*) color=7 ;; # white or invalid color
       esac
    fi
    tput setaf $color;
    echo $exp;
    tput sgr0;
}

function stopServers {
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
}

function resetDB {
    echo "********DROP THE DATABASE********"
    `mysql -u${mysqlUser} -p${mysqlPassword} -e"DROP DATABASE ifs"`
    `mysql -u${mysqlUser} -p${mysqlPassword} -e"CREATE DATABASE ifs CHARACTER SET utf8"`
}

function buildAndDeploy {
    echo "********BUILD AND DEPLOY THE APPLICATION********"
    cd ${dataServiceCodeDir}
    ## Before we start the build we need to have an webtest test build environment
    echo "********SWAPPING IN THE WEBTEST BUILD PROPERTIES********"
    sed 's/ext\.ifsFlywayLocations.*/ext\.ifsFlywayLocations="db\/migration,db\/webtest"/' dev-build.gradle > webtest.gradle.tmp
    mv dev-build.gradle dev-build.gradle.tmp
    mv webtest.gradle.tmp dev-build.gradle
    ./gradlew clean client clientCopy testCommonCode testCommonCodeCopy deployToTomcat
    ./gradlew flywayMigrate
    ## Replace the webtest build environment with the one we had before.
    echo "********SWAPPING BACK THE ORIGINAL BUILD PROPERTIES********"
    mv dev-build.gradle.tmp dev-build.gradle

    cd ${webServiceCodeDir}
    ./gradlew clean deployToTomcat
    

}

function startServers {
    echo "********START THE DATA SERVER********"
    cd ${dataTomcatBinPath}
    ./startup.sh
    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
    touch ${dataLogFilePath}
    tail -F -n0 ${dataLogFilePath} | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
    echo "********START THE WEB SERVER********"
    touch ${webTomcatBinPath}
    cd ${webTomcatBinPath}
    ./startup.sh
    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
    tail -F -n0 ${webLogFilePath} | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
}

function runTests {
    echo "**********RUN THE WEB TESTS**********"
    cd ${scriptDir}
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase --exclude Failing --exclude Pending --name IFS $testDirectory
}

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
echo "dataTomcatBinPath: ${dataTomcatBinPath}"
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
echo "webTomcatBinPath:  ${webTomcatBinPath}"
webLogPath=$webTomcatPath"/logs"
echo "webLogPath:        ${webLogPath}"
webLogFilePath=${webLogPath}"/catalina."${dateFormat}".log"
echo "webLogFilePath:    ${webLogFilePath}"
webPort=`sed '/^\#/d' dev-build.gradle | grep 'ext.serverPort'  | cut -d "=" -f2 | sed 's/"//g'`
echo "webPort:           ${webPort}"
webBase="localhost:"${webPort}
echo "webBase:           ${webBase}"

unset opt
unset quickTest

testDirectory='IFS_acceptance_tests/tests/*'
while getopts ":q :d:" opt ; do
    case $opt in
        q)
         quickTest=1
        ;;
        d)
         testDirectory="$OPTARG"
        ;;
        \?)
         coloredEcho "Invalid option: -$OPTARG" red >&2
         exit 1
        ;;
        :)
         case $OPTARG in
            d)
             coloredEcho "Option -$OPTARG requires the location of the robottest files relative to $scriptDir." red >&2
            ;;
            *)
             coloredEcho "Option -$OPTARG requires an argument." red >&2
            ;;
         esac
        exit 1
        ;;
    esac
done

if [ "$quickTest" ]
then
    echo "using quickTest:   TRUE" >&2
    resetDB
    runTests
else
    echo "using quickTest:   FALSE" >&2
    stopServers
    buildAndDeploy
    resetDB
    startServers
    runTests
fi










