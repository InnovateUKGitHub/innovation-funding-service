#!/bin/bash

sudo echo "Thanks for entering sudo!"

setEnv() {
    case $OSTYPE in
        darwin*)
            echo "Mac detected"
            eval $(docker-machine env default)
            ;;
        linux*)
            echo "Linux detected"
            ;;
        *)
            echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
            exit 1
            ;;
    esac
}

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

function addTestFiles {
    echo "***********Adding test files***************"
    echo "***********Making the quarantined directory ***************"
    docker-compose exec data mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    docker-compose exec data cp ${uploadFileDir}/8 ${virusScanQuarantinedFolder}/8
}


function buildAndDeploy {
    echo "********BUILD AND DEPLOY THE APPLICATION********"
    cd ${dataServiceCodeDir}
    ## Before we start the build we need to have an webtest test build environment
    echo "********SWAPPING IN THE WEBTEST BUILD PROPERTIES********"
    sed 's/ext\.ifsFlywayLocations.*/ext\.ifsFlywayLocations="db\/migration,db\/setup,db\/webtest"/' docker-build.gradle > webtest.gradle.tmp
    mv docker-build.gradle docker-build.gradle.tmp
    mv webtest.gradle.tmp docker-build.gradle
    ./gradlew -Pprofile=docker cleanDeploy -x test
    ./gradlew flywayMigrate
    ## Replace the webtest build environment with the one we had before.
    echo "********SWAPPING BACK THE ORIGINAL BUILD PROPERTIES********"
    mv docker-build.gradle.tmp docker-build.gradle

    cd ${webServiceCodeDir}
    ./gradlew -Pprofile=docker clean deployToTomcat

}

function resetLDAP {
    cd ${shibbolethScriptsPath}
    ./reset-shibboleth-users.sh
}

function startServers {
    pwd
    cd ../setup-files/scripts/docker
    ./clean.sh
    ./startup.sh
    wait
    cd ../shibboleth/ui
    # ./deploy-ui.sh

    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE DATASERVICE**********"
    tail -F -n0 < docker-compose logs data | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE WEBSERVICE**********"
    tail -F -n0 < docker-compose logs web | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
    resetLDAP
}


function runTests {
    echo "**********RUN THE WEB TESTS**********"
    cd ${scriptDir}

    if [ "$localMailSendingImplemented" ]
    then
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
    else
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS $testDirectory
    fi
}


function runHappyPathTests {
    echo "*********RUN THE HAPPY PATH TESTS ONLY*********"
    cd ${scriptDir}
    if [ "$localMailSendingImplemented" ]
    then
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
    else
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS $testDirectory
    fi
}

function runTestsRemotely {
    echo "***********RUNNING AGAINST THE IFS DEV SERVER...**********"
    cd ${scriptDir}
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_AUTH:ifs:Fund1ng -v SERVER_BASE:ifs.dev.innovateuk.org -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:'YES' -v LOCAL_MAIL_SENDING_IMPLEMENTED:'YES' -v UPLOAD_FOLDER:$uploadFileDir -v RUNNING_ON_DEV:'YES' --exclude Failing --exclude Pending --exclude FailingForDev --name IFS $testDirectory
}

setEnv
cd "$(dirname "$0")"
echo "********GETTING ALL THE VARIABLES********"
scriptDir=`pwd`
echo "scriptDir:        ${scriptDir}"
uploadFileDir=${scriptDir}"/upload_files"
cd ../setup-files/scripts/environments
shibbolethScriptsPath=$(pwd)
echo "shibbolethScriptsPath:        ${shibbolethScriptsPath}"

cd ../../../ifs-data-service
dateFormat=`date +%Y-%m-%d`
dataServiceCodeDir=`pwd`
echo "dataServiceCodeDir:${dataServiceCodeDir}"
dataWebappsPath=`sed '/^\#/d' docker-build.gradle | grep 'ext.tomcatWebAppsDir'  | cut -d "=" -f2 | sed 's/"//g'`
echo "dataWebappsPath:   ${dataWebappsPath}"
dataTomcatPath="$(dirname "$dataWebappsPath")"
echo "dataTomcatPath:    ${dataTomcatPath}"
dataTomcatBinPath=${dataTomcatPath}"/bin"
echo "dataTomcatBinPath: ${dataTomcatBinPath}"
dataLogPath=$dataTomcatPath"/logs"
echo "dataLogPath:       ${dataLogPath}"
dataLogFilePath=${dataLogPath}"/catalina."${dateFormat}".log"
echo "dataLogFilePath:   ${dataLogFilePath}"
dataPort=`sed '/^\#/d' docker-build.gradle | grep 'ext.serverPort'  | cut -d "=" -f2 | sed 's/"//g'`
echo "dataPort:          ${dataPort}"
mysqlUser=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsDatasourceUsername'  | cut -d "=" -f2 | sed 's/"//g'`
echo "mysqlUser:         ${mysqlUser}"
mysqlPassword=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsDatasourcePassword'  | cut -d "=" -f2 | sed 's/"//g'`
baseFileStorage=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsFileStorageLocation'  | cut -d "=" -f2 | sed 's/"//g'`
ifsDatasourceHost=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsDatasourceHost'  | cut -d "=" -f2 | sed 's/"//g'`
echo "${baseFileStorage}"
storedFileFolder=${baseFileStorage}/ifs/
echo "${storedFileFolder}"
virusScanHoldingFolder=${baseFileStorage}/virus-scan-holding/
echo "virusScanHoldingFolder:		${virusScanHoldingFolder}"
virusScanQuarantinedFolder=${baseFileStorage}/virus-scan-quarantined
echo "virusScanQuarantinedFolder:		${virusScanQuarantinedFolder}"
virusScanScannedFolder=${baseFileStorage}/virus-scan-scanned
echo "virusScanScannedFolder:		${virusScanScannedFolder}"
echo "We are about to delete the above directories, make sure that they are right ones!"
postcodeLookupKey=`sed '/^\#/d' docker-build.gradle | grep 'ext.postcodeLookupKey'  | cut -d "=" -f2 | sed 's/"//g'`
echo "Postcode Lookup: 		${postcodeLookupKey}"
if [ "$postcodeLookupKey" = '' ]
then
    echo "Postcode lookup not implemented"
    postcodeLookupImplemented='NO'
else
    echo "Postcode lookup implemented. The tests will expect proper data from the SuT."
    postcodeLookUpImplemented='YES'
fi
sendMailLocally=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsSendMailLocally'  | cut -d "=" -f2 | sed 's/"//g'`
if [ $sendMailLocally = 'false' ]
then
    echo "Sending mail locally not implemented"
    unset localMailSendingImplemented
else
    echo "Sending mail locally is implemented. The tests will expect emails to be sent out to all whitelisted recipients. Please take care not to spam anyone!"
    localMailSendingImplemented='YES'
fi
cd ../ifs-web-service
webServiceCodeDir=`pwd`
echo "webServiceCodeDir: ${webServiceCodeDir}"
webPort=`sed '/^\#/d' docker-build.gradle | grep 'ext.serverPort'  | cut -d "=" -f2 | sed 's/"//g'`
echo "webPort:           ${webPort}"
webBase="ifs-local-dev"
echo "webBase:           ${webBase}"


unset opt
unset quickTest
unset testScrub
unset happyPath
useXvfb=true
unset remoteRun
unset startServersInDebugMode


testDirectory='IFS_acceptance_tests/tests/*'
while getopts ":q :t :h :p :r :d: :D :x" opt ; do
    case $opt in
        q)
         quickTest=1
        ;;
	t)
	 testScrub=1
	;;
	h)
	 happyPath=1
	;;
	p)
	 happyPathTestOnly=1
	;;
	x)
	 useXvfb=false
	;;
        r)
         remoteRun=1
        ;;
        d)
         testDirectory="$OPTARG"
        ;;
        D)
         startServersInDebugMode=true
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
    resetLDAP
    addTestFiles
    runTests
elif [ "$testScrub" ]
then
    echo "using testScrub mode: this will do all the dirty work but omit the tests" >&2
    buildAndDeploy
    startServers
    addTestFiles
elif [ "$happyPath" ]
then
    echo "using happyPath mode: this will run a pared down set of tests as a sanity check for developers pre-commit" >&2
    buildAndDeploy
    startServers
    addTestFiles
    runHappyPathTests
elif [ "$happyPathTestOnly" ]
then
    echo "run test only"
    runHappyPathTests
elif [ "$remoteRun" ]
then
    echo "Pointing the tests at the ifs dev server - note that some tests may fail if you haven't scrubbed the dev server's db" >&2
    runTestsRemotely
else
    echo "using quickTest:   FALSE" >&2
    buildAndDeploy
    startServers
    addTestFiles
    runTests
fi