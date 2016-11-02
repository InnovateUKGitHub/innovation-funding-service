#!/bin/bash

# Define some functions for later use

function coloredEcho() {
    local exp=$1;
    local color=$2;
    if ! [[ ${color} =~ '^[0-9]$' ]] ; then
       case $(echo ${color} | tr '[:upper:]' '[:lower:]') in
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
    tput setaf ${color};
    echo ${exp};
    tput sgr0;
}

function section() {
    echo
    coloredEcho "$1" green
    echo
}

function startContainers() {
    section "=> STARTING DOCKER CONTAINERS"

    cd ${dockerDir}
    docker-compose -p ifs start
}

function addTestFiles() {
    section "=> CLEANING TEST FILE REPOSITORIES IN DOCKER CONTAINER"

    echo "storedFileFolder:                 ${storedFileFolder}"
    echo "virusScanHoldingFolder:           ${virusScanHoldingFolder}"
    echo "virusScanQuarantinedFolder:       ${virusScanQuarantinedFolder}"
    echo "virusScanScannedFolder:           ${virusScanScannedFolder}"
    echo

    echo "=> Deleting any uploaded files..."
    docker exec ifs_data_1 rm -rf ${storedFileFolder}

    echo "=> Deleting any holding for scan files..."
    docker exec ifs_data_1 rm -rf ${virusScanHoldingFolder}

    echo "=> Deleting any quarantined files..."
    docker exec ifs_data_1 rm -rf ${virusScanQuarantinedFolder}

    echo "=> Deleting any scanned files..."
    docker exec ifs_data_1 rm -rf ${virusScanScannedFolder}

    section "=> ADDING TEST FILES INTO DOCKER CONTAINER"

    echo "=> Making the quarantined directory..."
    docker exec ifs_data_1 mkdir -p ${virusScanQuarantinedFolder}
    echo "=> Adding pretend quarantined file..."
    docker cp ${uploadFileDir}/8 ifs_data_1:${virusScanQuarantinedFolder}/8
}

function resetDB() {
    section "=> RESETTING DATABASE STATE"

    cd ${dataServiceCodeDir}
    ./gradlew -Pprofile=docker flywayClean flywayMigrate

    echo "=> Syncing Shib users..."
    # Sync the users in Shibboleth whilst we're doing this
    cd ${dockerDir}
    ./syncShib.sh
}

function buildAndDeploy() {
    section "=> BUILDING AND DEPLOYING APPLICATION"

    if [[ ${noDeploy} -eq 1 ]]
    then
        coloredEcho "=> No Deploy flag used. Skipping build and deploy..." yellow
        return
    fi

    cd ${dataServiceCodeDir}

    ## Before we start the build we need to have an webtest test build environment
    echo "=> Swapping in the webtest build properties..."

    sed 's/ext\.ifsFlywayLocations.*/ext\.ifsFlywayLocations="db\/migration,db\/setup,db\/webtest"/' docker-build.gradle > webtest.gradle.tmp
    mv docker-build.gradle docker-build.gradle.tmp
    mv webtest.gradle.tmp docker-build.gradle

    echo "=> Starting build and deploy script..."

    cd ${dockerDir}
    ./deploy.sh all
    wait
    sleep 5

    ## Replace the webtest build environment with the one we had before.
    echo "=> Swapping back original build properties..."
    cd ${dataServiceCodeDir}
    mv docker-build.gradle.tmp docker-build.gradle

#    cd ${webServiceCodeDir}
#    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE DATASERVICE**********"
#    docker-compose -p ifs logs -ft --tail 10 data | while read logLine
#    do
#      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
#    done
#    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE WEBSERVICE**********"
#    docker-compose -p ifs logs -ft --tail 10 web | while read logLine
#    do
#      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
#    done
}

function startSeleniumGrid() {
    section "=> STARTING SELENIUM GRID"

    cd ${scriptDir}

    if [[ ${parallel} -eq 1 ]]
    then
      declare -i suiteCount=$(find ${testDirectory}/* -maxdepth 0 -type d | wc -l)
    else
      declare -i suiteCount=1
    fi
    if [[ ${suiteCount} -eq 0 ]]
    then
      suiteCount=1
    fi

    echo "=> Suite count: ${suiteCount}"

    docker-compose -p robot up -d --force-recreate --build
    docker-compose -p robot scale chrome=${suiteCount}

    unset suiteCount
    if [[ ${quickTest} -eq 1 ]]
    then
      echo "=> Waiting 5 seconds for the grid to be properly started"
      sleep 5
    fi
  }

function stopSeleniumGrid() {
    section "=> STOPPING SELENIUM GRID"

    cd ${scriptDir}
    docker-compose -p robot down -v --remove-orphans
}

function startPybot() {
    section "=> STARTING PYBOT FOR ${1}"

    targetDir=`basename ${1}`

    if [[ "$happyPath" ]]
      then
        local includeHappyPath='--include HappyPath'
      else
        local includeHappyPath=''
    fi
    if [[ ${emails} -eq 1 ]]
      then
        local emailsString='--exclude Email'
      else
        local emailsString=''
    fi
    if [[ ${rerunFailed} -eq 1 ]]; then
      local rerunString='--rerunfailed target/${targetDir}/output.xml --output rerun.xml'
    else
      local rerunString=''
    fi

    pybot --outputdir target/${targetDir} ${rerunString} --pythonpath IFS_acceptance_tests/libs \
    -v docker:1 \
    -v SERVER_BASE:${webBase} \
    -v PROTOCOL:'https://' \
    -v POSTCODE_LOOKUP_IMPLEMENTED:${postcodeLookupImplemented} \
    -v UPLOAD_FOLDER:${uploadFileDir} \
    -v DOWNLOAD_FOLDER:download_files \
    -v BROWSER=chrome \
    -v REMOTE_URL:'http://ifs-local-dev:4444/wd/hub' \
    $includeHappyPath \
    --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal ${emailsString} --name ${targetDir} ${1} &
}

function runTests() {
    section "=> RUNNING THE WEB TESTS"

    cd ${scriptDir}

    if [[ ${parallel} -eq 1 ]]
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

    if [[ ${parallel} -eq 1 ]]
    then
      results=`find target/* -regex ".*/output\.xml"`
      rebot -d target ${results}
    fi
}

function clearOldReports() {
  section "=> REMOVING OLD REPORTS"
  rm -rf target
  mkdir target
}

# ====================================
# The actual start point of our script
# ====================================

section "=> GETTING SCRIPT VARIABLES"

#cd "$(dirname "$0")"
scriptDir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd ${scriptDir}
cd ..

rootDir=`pwd`
dockerDir="${rootDir}/setup-files/scripts/docker-native"

dataServiceCodeDir="${rootDir}/ifs-data-service"
webServiceCodeDir="${rootDir}/ifs-web-service"
webBase="ifs-local-dev"

uploadFileDir="${scriptDir}/upload_files"
baseFileStorage="/tmp/uploads"
storedFileFolder="${baseFileStorage}/ifs/"
virusScanHoldingFolder="${baseFileStorage}/virus-scan-holding/"
virusScanQuarantinedFolder="${baseFileStorage}/virus-scan-quarantined"
virusScanScannedFolder="${baseFileStorage}/virus-scan-scanned"

postcodeLookupKey=`sed '/^\#/d' ${dataServiceCodeDir}/docker-build.gradle | grep 'ext.postcodeLookupKey'  | cut -d "=" -f2 | sed 's/"//g'`

echo "scriptDir:                    ${scriptDir}"
echo "rootDir:                      ${rootDir}"
echo "dockerDir:                    ${dockerDir}"
echo "dataServiceCodeDir:           ${dataServiceCodeDir}"
echo "webServiceCodeDir:            ${webServiceCodeDir}"
echo "webBase:                      ${webBase}"
echo "uploadFileDir:                ${uploadFileDir}"
echo
echo "baseFileStorage:              ${baseFileStorage}"
echo "virusScanHoldingFolder:       ${virusScanHoldingFolder}"
echo "virusScanQuarantinedFolder:   ${virusScanQuarantinedFolder}"
echo "virusScanScannedFolder:       ${virusScanScannedFolder}"

echo
coloredEcho "=> We are about to delete the above storage directories in Docker, make sure that they are right ones!" yellow
echo

if [ "${postcodeLookupKey}" = '' ]
then
    coloredEcho "=> Postcode lookup not implemented" blue
    postcodeLookupImplemented='NO'
else
    echo "postcodeLookupKey:        ${postcodeLookupKey}"
    coloredEcho "=> Postcode lookup implemented. The tests will expect proper data from the SuT." blue
    postcodeLookUpImplemented='YES'
fi

unset opt
unset testScrub

quickTest=0
emails=0
rerunFailed=0
parallel=0
stopGrid=0
noDeploy=0

testDirectory='IFS_acceptance_tests/tests'
while getopts ":p :q :h :t :e :r :d :c :n:" opt ; do
    case ${opt} in
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
        r)
		    rerunFailed=1
    	;;
    	d)
            testDirectory="$OPTARG"
            parallel=0
        ;;
        c)
            stopGrid=1
        ;;
        n)
            noDeploy=1
        ;;
        \?)
            coloredEcho "=> Invalid option: -$OPTARG" red >&2
            exit 1
        ;;
        :)
            case $OPTARG in
           	    d)
                 coloredEcho "=> Option -$OPTARG requires the location of the robottest files relative to ${scriptDir}." red >&2
                ;;
                *)
                 coloredEcho "=> Option -$OPTARG requires an argument." red >&2
                ;;
            esac
        exit 1
        ;;
    esac
done

startSeleniumGrid

if [[ ${rerunFailed} -eq 0 ]]
then
    clearOldReports
fi

if [[ ${quickTest} -eq 1 ]]
then
    coloredEcho "=> Using quickTest: TRUE" blue

    runTests
elif [[ ${testScrub} ]]
then
    coloredEcho "=> Using testScrub mode: this will do all the dirty work but omit the tests" blue

    startContainers
    resetDB
    addTestFiles
    buildAndDeploy
else
    coloredEcho "=> Using quickTest: FALSE" blue

    startContainers
    resetDB
    addTestFiles
    buildAndDeploy
    runTests
fi

if [[ ${stopGrid} -eq 1 ]]
then
    stopSeleniumGrid
fi

if [[ $(which google-chrome) ]]
then
    google-chrome target/${targetDir}/log.html &
else
    wd=$(pwd)
    logs="target/${targetDir}/log.html"
    open "file://${wd}/${logs}"
fi