#!/bin/bash

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
    ./gradlew -Pprofile=docker flywayMigrate
    ## Replace the webtest build environment with the one we had before.
    echo "********SWAPPING BACK THE ORIGINAL BUILD PROPERTIES********"
    mv docker-build.gradle.tmp docker-build.gradle

    cd ${webServiceCodeDir}
    ./gradlew -Pprofile=docker clean deployToTomcat

}

function resetLDAP {
    cd ../setup-files/scripts/docker
    ./syncShib.sh
}

function startServers {
    pwd
    cd ../setup-files/scripts/docker
    ./startup.sh
    ./deploy.sh -x test
    ./syncShib.sh
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

function startSeleniumGrid {
    cd ../robot-tests
    cd ${testDirectory}
    cd ${scriptDir}
    docker-compose up -d
    docker-compose scale chrome=5
}

function startPybot {
    echo "********** starting pybot for ${1} **************"
    targetDir=`basename ${1}`
    pybot --outputdir target/${targetDir} --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v BROWSER=chrome -v unsuccessful_login_message:'Your login was unsuccessful because of the following issue(s)' --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS ${1}/* &
}

function runTests {
    echo "**********RUN THE WEB TESTS**********"
    cd ${scriptDir}

    for D in `find ${testDirectory}/* -maxdepth 0 -type d`
    do
        startPybot ${D}
    done

    for job in `jobs -p`
    do
        wait $job
    done

    results=`find target/* -regex ".*/output\.xml"`
    rebot -d target ${results}
}

function runTestsRemotely {
    echo "***********RUNNING AGAINST THE IFS DEV SERVER...**********"
    cd ${scriptDir}
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_AUTH:ifs:Fund1ng -v SERVER_BASE:ifs.dev.innovateuk.org -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:'YES' -v LOCAL_MAIL_SENDING_IMPLEMENTED:'YES' -v UPLOAD_FOLDER:$uploadFileDir -v RUNNING_ON_DEV:'YES' --exclude Failing --exclude Pending --exclude FailingForDev --name IFS ${testDirectory}/*
}

setEnv
cd "$(dirname "$0")"
echo "********GETTING ALL THE VARIABLES********"
scriptDir=`pwd`
echo "scriptDir:        ${scriptDir}"
uploadFileDir=${scriptDir}"/upload_files"
cd ../ifs-data-service
dataServiceCodeDir=`pwd`
echo "dataServiceCodeDir:${dataServiceCodeDir}"
baseFileStorage=`sed '/^\#/d' docker-build.gradle | grep 'ext.ifsFileStorageLocation'  | cut -d "=" -f2 | sed 's/"//g'`
echo "${baseFileStorage}"
virusScanQuarantinedFolder=${baseFileStorage}/virus-scan-quarantined
echo "virusScanQuarantinedFolder:		${virusScanQuarantinedFolder}"
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
cd ../ifs-web-service
webServiceCodeDir=`pwd`
echo "webServiceCodeDir: ${webServiceCodeDir}"
webBase="ifs-local-dev"
echo "webBase:           ${webBase}"


unset opt
unset quickTest
unset testScrub
unset remoteRun
unset startServersInDebugMode


testDirectory='IFS_acceptance_tests/tests'
while getopts ":q :t :r :d: :D" opt ; do
    case $opt in
        q)
         quickTest=1
        ;;
        t)
         testScrub=1
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

startSeleniumGrid

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