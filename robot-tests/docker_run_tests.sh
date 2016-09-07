#!/bin/bash

function setEnv() {
    echo "setting environment"
    eval $(docker-machine env default)
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

function addTestFiles() {
    echo "***********Adding test files***************"
    echo "***********Making the quarantined directory ***************"
    docker-compose -p ifs exec data mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    docker-compose -p ifs exec data cp -R ${uploadFileDir}/8 ${virusScanQuarantinedFolder}/8
}

function resetLDAP() {
    cd ../setup-files/scripts/docker
    ./syncShib.sh
}

function resetDB(){
  cd ${dataServiceCodeDir}
  ./gradlew -Pprofile=docker flywayClean flywayMigrate
  resetLDAP
}

function buildAndDeploy() {
    echo "********BUILD AND DEPLOY THE APPLICATION********"
    cd ${dataServiceCodeDir}
    ## Before we start the build we need to have an webtest test build environment
    echo "********SWAPPING IN THE WEBTEST BUILD PROPERTIES********"
    sed 's/ext\.ifsFlywayLocations.*/ext\.ifsFlywayLocations="db\/migration,db\/setup,db\/webtest"/' docker-build.gradle > webtest.gradle.tmp
    mv docker-build.gradle docker-build.gradle.tmp
    mv webtest.gradle.tmp docker-build.gradle
    ./gradlew -Pprofile=docker clean deployToTomcat
    ## Replace the webtest build environment with the one we had before.
    echo "********SWAPPING BACK THE ORIGINAL BUILD PROPERTIES********"
    mv docker-build.gradle.tmp docker-build.gradle

    cd ${webServiceCodeDir}
    ./gradlew -Pprofile=docker clean deployToTomcat

    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE DATASERVICE**********"
    docker-compose -p ifs logs -ft --tail 10 data | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE WEBSERVICE**********"
    docker-compose -p ifs logs -ft --tail 10 web | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done

}

function startServers() {
    cd ../setup-files/scripts/docker
    ./startup.sh
    wait
}

function startSeleniumGrid() {
    cd ../robot-tests
    cd ${testDirectory}
    cd ${scriptDir}

    if [ "$parallel" ]
    then
      declare -i suiteCount=$(find ${testDirectory}/* -maxdepth 0 -type d | wc -l)
    else
      declare -i suiteCount=1
    fi
    echo ${suiteCount}
    docker-compose -p robot up -d
    docker-compose -p robot scale chrome=${suiteCount}
    unset suiteCount
}

function stopSeleniumGrid() {
    cd ../robot-tests
    cd ${testDirectory}
    cd ${scriptDir}
    docker-compose -p robot down -v --remove-orphans
}

function startPybot() {
    echo "********** starting pybot for ${1} **************"
    targetDir=`basename ${1}`
    if [ "$happyPath" ]
      then
        local includeHappyPath='--include HappyPath'
      else
        local includeHappyPath=''
    fi
    if [ $emails -eq 1 ]
      then
        local excludeEmails=''
      else
        local excludeEmails='--exclude Email'
    fi
    pybot --outputdir target/${targetDir} --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:${postcodeLookupImplemented} -v UPLOAD_FOLDER:${uploadFileDir} -v DOWNLOAD_FOLDER:download_files -v BROWSER=chrome -v REMOTE_URL:'http://ifs-local-dev:4444/wd/hub' ${includeHappyPath} --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal ${excludeEmails} --name ${targetDir} ${1}/* &
}

function runTests() {
    echo "**********RUN THE WEB TESTS**********"
    cd ${scriptDir}

    if [ "$parallel" ]
    then
      for D in `find ${testDirectory}/* -maxdepth 0 -type d`
      do
          startPybot ${D}
      done
    else
      startPybot ${testDirectory}
    fi

    for job in `jobs -p`
    do
        wait $job
    done

    if [ "$parallel" ]
    then
      results=`find target/* -regex ".*/output\.xml"`
      rebot -d target ${results}
    fi
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
unset parallel
unset emails

emails=0

testDirectory='IFS_acceptance_tests/tests'
while getopts ":p :h :q :t :e" opt ; do
    case $opt in
        p)
         parallel=1
        ;;
        q)
         quickTest=1
        ;;
        h)
         happyPath=1
        ;;
        t)
         testScrub=1
        ;;
        e)
         emails=1
        ;;
        \?)
         coloredEcho "Invalid option: -$OPTARG" red >&2
         exit 1
        ;;
        :)
         case $OPTARG in
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
    #resetDB
    #addTestFiles
    runTests
elif [ "$testScrub" ]
then
    echo "using testScrub mode: this will do all the dirty work but omit the tests" >&2
    startServers
    #buildAndDeploy
    resetDB
    addTestFiles
else
    echo "using quickTest:   FALSE" >&2
    startServers
    #buildAndDeploy toDO: fix this with docker
    resetDB
    addTestFiles
    runTests
fi

stopSeleniumGrid
