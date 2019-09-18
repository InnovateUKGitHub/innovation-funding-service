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

function clearDownFileRepository() {
    echo "***********Deleting any uploaded files***************"
    echo "storedFileFolder:   ${storedFileFolder}"
    docker exec -d data-service  rm -rf ${storedFileFolder}

    echo "***********Deleting any holding for scan files***************"
    echo "virusScanHoldingFolder: ${virusScanHoldingFolder}"
    docker exec -d data-service  rm -rf ${virusScanHoldingFolder}

    echo "***********Deleting any quarantined files***************"
    echo "virusScanQuarantinedFolder: ${virusScanQuarantinedFolder}"
    docker exec -d data-service  rm -rf ${virusScanQuarantinedFolder}

    echo "***********Deleting any scanned files***************"
    echo "virusScanScannedFolder: ${virusScanScannedFolder}"
    docker exec -d data-service  rm -rf ${virusScanScannedFolder}
}

function addTestFiles() {
    section "=> RESETTING FILE STORAGE STATE"

    clearDownFileRepository
    echo "***********Adding test files***************"
    docker exec -d data-service  cp ${uploadFileDir}/testing.pdf /tmp/testing.pdf

    echo "***********Making the quarantined directory ***************"
    docker exec -d data-service mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    docker exec -d data-service cp /tmp/testing.pdf ${virusScanQuarantinedFolder}/8

    echo "***********Adding standard file upload location ***********"
    docker exec -d data-service mkdir -p ${storedFileFolder}/000000000_999999999/000000_999999/000_999

    echo "***********Creating file entry for each db entry***********"
    max_file_entry_id=$(docker exec ifs-database mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')
    for i in `seq 1 ${max_file_entry_id}`;
    do
      if [ "${i}" != "8" ]
      then
        docker exec -d data-service cp /tmp/testing.pdf ${storedFileFolder}/000000000_999999999/000000_999999/000_999/${i}
      fi
    done
}

function initialiseTestEnvironment() {

    cd ${rootDir}

    if [[ ${quickTest} -eq 1 ]]
      then
        section "=> STARTING SELENIUM GRID and INJECTING ENVIRONMENT PARAMETERS"
        ./gradlew :robot-tests:deployHub :robot-tests:deployChrome :robotTestsFilter --configure-on-demand

        echo "=> Waiting 5 seconds for the grid to be properly started"
        sleep 5
      else
        section "=> STARTING SELENIUM GRID, INJECTING ENVIRONMENT PARAMETERS, RESETTING DATABASE STATE"
        ./gradlew :robot-tests:deployHub :robot-tests:deployChrome :robotTestsFilter :ifs-data-layer:ifs-data-service:flywayClean :ifs-data-layer:ifs-data-service:flywayMigrate --configure-on-demand
        section "=> SYNCING SHIBBOLETH USERS"
        ./gradlew :ifs-data-layer:ifs-data-service:syncShib 2>&1 >/dev/null
    fi

  }

function stopSeleniumGrid() {
    section "=> STOPPING SELENIUM GRID"

    cd ${rootDir}
    ./gradlew :robot-tests:removeHub :robot-tests:removeChrome --configure-on-demand
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
    if [[ "$useBespokeIncludeTags" ]]
      then
        for includeTag in $bespokeIncludeTags; do
            local includeBespokeTags+=' --include '${includeTag}
        done
      else
        local includeBespokeTags=''
    fi
    if [[ "$useBespokeExcludeTags" ]]
      then
          for excludeTag in $bespokeExcludeTags; do
              local excludeBespokeTags+=' --exclude '${excludeTag}
          done
      else
        local excludeBespokeTags=''
    fi
    if [[ ${rerunFailed} -eq 1 ]]; then
      local rerunString='--rerunfailed target/${targetDir}/output.xml --output rerun.xml'
    else
      local rerunString=''
    fi
    if [[ ${dryRun} -eq 1 ]]
      then
        local dryRunString='--dryrun'
      else
        local dryRunString=''
    fi
    if [[ ${eu} -eq 1 ]]
      then
        local includeEuTags='--include EU2020'
      else
        local includeEuTags='--exclude EU2020'
    fi


    python3 -m robot --outputdir target/${targetDir} ${rerunString} ${dryRunString} --pythonpath IFS_acceptance_tests/libs \
    -v docker:1 \
    -v SERVER_BASE:${webBase} \
    -v PROTOCOL:'https://' \
    -v POSTCODE_LOOKUP_IMPLEMENTED:${postcodeLookupImplemented} \
    -v UPLOAD_FOLDER:${uploadFileDir} \
    -v DOWNLOAD_FOLDER:download_files \
    -v BROWSER=chrome \
    -v REMOTE_URL:'http://ifs.local-dev:4444/wd/hub' \
    -v SAUCELABS_RUN:0 \
    -v local_imap:'ifs.local-dev' \
    -v local_imap_port:9876 \
    $includeHappyPath \
    $includeBespokeTags \
    $excludeBespokeTags \
    $includeEuTags \
    --exclude Failing --exclude Pending --name ${targetDir} ${1} &
}

function runTests() {
    section "=> RUNNING THE WEB TESTS"

    cd ${scriptDir}

    startPybot ${testDirectory}

    if [[ $vnc -eq 1 ]]
    then
      if [ "$(uname)" == "Darwin" ];
      then
        open "vnc://root:secret@ifs.local-dev:5900"
      fi
      echo "**********For remote desktop please use this url in your vnc client**********"
        echo  "vnc://root:secret@ifs.local-dev:5900"
    fi

    for job in `jobs -p`
    do
        wait $job
    done
}

function deleteEmails() {
    section "=> SCRUBBING DOWN THE TEST MAILBOXES"
    cd ${scriptDir}
    python3 -m robot --outputdir target/set_up_steps --pythonpath IFS_acceptance_tests/libs \
    -v docker:1 \
    -v local_imap:'ifs.local-dev' \
    -v local_imap_port:9876  \
    IFS_acceptance_tests/tests/00__Set_Up_Tests/delete_emails.robot 2>&1 >/dev/null
    echo "...done"
}

function clearOldReports() {
  section "=> REMOVING OLD REPORTS"
  cd ${scriptDir}
  rm -rf target
  mkdir target
}

function getZAPReport() {
    curl --silent ifs.local-dev:9000/OTHER/core/other/htmlreport/ -o target/${targetDir}/ZAPReport.html
}

function saveResultsToCompressedFolder() {
  # compresses the results as a tar using the current branch name
  branchName=$(git rev-parse --abbrev-ref HEAD)
  replace=""
  removeFeature="${branchName/feature\//$replace}"
  removeBugfix="${removeFeature/bugfix\//$replace}"
  removeHotfix="${removeBugfix/hotfix\//$replace}"
  branchName=${removeHotfix}
  dt=$(date '+%d-%m-%Y_%Hh%Mm');
  mkdir -p results
  tar -zcf "results/"${branchName}"-"${dt}.tar.gz "target/"${targetDir}
}

function openReports() {
if [[ $(which google-chrome) ]]
then
   google-chrome target/${targetDir}/log.html &
   if [[ ${showZapReport} -eq 1 ]]
   then
        google-chrome target/${targetDir}/ZAPReport.html &
   fi
else
   wd=$(pwd)
   logs="target/${targetDir}"
   open "file://${wd}/${logs}/log.html"
   if [[ ${showZapReport} -eq 1 ]]
   then
       open "file://${wd}/${logs}/ZAPReport.html"
   fi
fi
}


# ====================================
# The actual start point of our script
# ====================================

section "=> GETTING SCRIPT VARIABLES"

scriptDir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd ${scriptDir}
cd ..

rootDir=`pwd`

dataServiceCodeDir="${rootDir}/ifs-data-layer/ifs-data-service"
webServiceCodeDir="${rootDir}/ifs-web-service"
webBase="ifs.local-dev"

uploadFileDir="${scriptDir}/upload_files"
baseFileStorage="/tmp"
storedFileFolder="${baseFileStorage}/ifs/"
virusScanHoldingFolder="${baseFileStorage}/virus-scan-holding/"
virusScanQuarantinedFolder="${baseFileStorage}/virus-scan-quarantined"
virusScanScannedFolder="${baseFileStorage}/virus-scan-scanned"

postcodeLookupKey=`sed '/^\#/d' ${dataServiceCodeDir}/docker-build.gradle | grep 'ext.postcodeLookupKey'  | cut -d "=" -f2 | sed 's/"//g'`

echo "scriptDir:                    ${scriptDir}"
echo "rootDir:                      ${rootDir}"
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
unset useBespokeIncludeTag
unset useBespokeExcludeTag

quickTest=0
rerunFailed=0
stopGrid=0
showZapReport=0
compress=0
eu=0
dryRun=0
openReports=1

testDirectory='IFS_acceptance_tests/tests'
while getopts ":q :h :t :r :c :w :z :d: :x :R :B :I: :E: :o" opt ; do
    case ${opt} in
        q)
            quickTest=1
        ;;
        h)
            happyPath=1
        ;;
        t)
            testScrub=1
        ;;
        r)
		    rerunFailed=1
    	;;
    	z)
    	    showZapReport=1
        ;;
    	d)
            testDirectory="$OPTARG"
        ;;
        I)
            useBespokeIncludeTags=1
            bespokeIncludeTags+="$OPTARG "
        ;;
        E)
            useBespokeExcludeTags=1
            bespokeExcludeTags+="$OPTARG "
        ;;
        c)
            stopGrid=1
        ;;
        w)
          vnc=1
        ;;
        x)
          compress=1
        ;;
        R)
          dryRun=1
        ;;
        B)
          eu=1
        ;;
	o)
	  openReports=0
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

initialiseTestEnvironment

if [[ ${rerunFailed} -eq 0 ]]
then
    clearOldReports
fi

if [[ ${quickTest} -eq 1 ]]
then
    coloredEcho "=> Using quickTest: TRUE" blue
    addTestFiles
    deleteEmails
    runTests
elif [[ ${testScrub} ]]
then
    coloredEcho "=> Using testScrub mode: this will do all the dirty work but omit the tests" blue
    addTestFiles
    deleteEmails
else
    coloredEcho "=> Using quickTest: FALSE" blue
    addTestFiles
    deleteEmails
    runTests
fi

getZAPReport

if [[ ${stopGrid} -eq 1 ]]
then
    stopSeleniumGrid
fi

if [[ ${openReports} -eq 1 ]]
then
    openReports
fi

if [[ ${compress} -eq 1 ]]
then
  saveResultsToCompressedFolder
fi
