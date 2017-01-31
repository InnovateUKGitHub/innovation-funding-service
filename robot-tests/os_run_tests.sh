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
    -v REMOTE_URL:'http://hub:4444/wd/hub' \
    $includeHappyPath \
    $includeBespokeTags \
    $excludeBespokeTags \
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

    if [[ $vnc -eq 1 ]]
    then
      local vncport="$(docker-compose -f docker-compose-services.yml port chrome 5900)"
      vncport=${vncport:8:5}

      if [ "$(uname)" == "Darwin" ];
      then
        open "vnc://root:secret@ifs-local-dev:"${vncport}
      fi
      echo "**********For remote desktop please use this url in your vnc client**********"
        echo  "vnc://root:secret@ifs-local-dev:"${vncport}
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

dataServiceCodeDir="${rootDir}/ifs-data-service"
webServiceCodeDir="${rootDir}/ifs-web-service"
webBase="<<SHIB-ADDRESS>>"

uploadFileDir="${scriptDir}/upload_files"
baseFileStorage="/tmp/uploads"
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
emails=0
rerunFailed=0
parallel=0
stopGrid=0
noDeploy=0

testDirectory='IFS_acceptance_tests/tests'
while getopts ":p :q :h :t :r :c :n :w :d: :I: :E:" opt ; do
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
        n)
            noDeploy=1
        ;;
        w)
          vnc=1
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

#startSeleniumGrid

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

    buildAndDeploy
    resetDB
    addTestFiles
else
    coloredEcho "=> Using quickTest: FALSE" blue

    buildAndDeploy
    resetDB
    addTestFiles
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
    #open "file://${wd}/${logs}"
fi


echo "DONE"
sleep 1000000000000
