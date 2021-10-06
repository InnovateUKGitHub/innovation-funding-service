#!/bin/bash

# Define some functions for later use
DATASERVICE_POD=$(kubectl get pod -l app=data-service -o jsonpath="{.items[0].metadata.name}")

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
    kubectl exec $DATASERVICE_POD -- rm -rf ${storedFileFolder}

    echo "***********Deleting any holding for scan files***************"
    echo "virusScanHoldingFolder: ${virusScanHoldingFolder}"
    kubectl exec $DATASERVICE_POD -- rm -rf ${virusScanHoldingFolder}

    echo "***********Deleting any quarantined files***************"
    echo "virusScanQuarantinedFolder: ${virusScanQuarantinedFolder}"
    kubectl exec $DATASERVICE_POD -- rm -rf ${virusScanQuarantinedFolder}

    echo "***********Deleting any scanned files***************"
    echo "virusScanScannedFolder: ${virusScanScannedFolder}"
    kubectl exec $DATASERVICE_POD -- rm -rf ${virusScanScannedFolder}
}

function addTestFiles() {
    section "=> RESETTING FILE STORAGE STATE"

    clearDownFileRepository
    echo "***********Adding test files***************"
    kubectl cp ${uploadFileDir}/testing.pdf $DATASERVICE_POD:/tmp/

    echo "***********Making the quarantined directory ***************"
    kubectl exec $DATASERVICE_POD -- mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    kubectl exec $DATASERVICE_POD -- cp /tmp/testing.pdf ${virusScanQuarantinedFolder}/8

    echo "***********Adding standard file upload location ***********"
    kubectl exec $DATASERVICE_POD -- mkdir -p ${storedFileFolder}/000000000_999999999/000000_999999/000_999

    echo "***********Creating file entry for each db entry***********"
    MYSQL_POD=$(kubectl get pod -l app=ifs-database -o jsonpath="{.items[0].metadata.name}")
    max_file_entry_id=$(kubectl exec $MYSQL_POD -- mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')
    for i in `seq 1 ${max_file_entry_id}`;
    do
      if [ "${i}" != "8" ]
      then
        kubectl exec $DATASERVICE_POD -- cp /tmp/testing.pdf ${storedFileFolder}/000000000_999999999/000000_999999/000_999/${i}
      fi
    done
}

k8s_delete() {
  pod=$(kubectl get pod -l app="$1" -o name)
  kubectl delete $pod
}

k8s_rebuild_db() {
  k8s_delete cache-provider
  k8s_wait cache-provider
  k8s_delete ldap
  k8s_wait ldap
  k8s_delete ifs-database
  k8s_wait ifs-database
  k8s_delete data-service
  k8s_wait data-service

}

k8s_wait() {
  while [[ $(kubectl get pods -l app=$1 -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]];
    do echo "waiting for pod $1" && sleep 5;
  done;
}

k8s_sync_ldap() {
  if [[ -z "${TEST_USER_PASSWORD}" ]]; then
    echo 'IFS_TEST_USER_PASSWORD env var is not set so using default of Passw0rd1357'
    pass=$(slappasswd -s "Passw0rd1357" | base64)
  else
    echo 'IFS_TEST_USER_PASSWORD is set as env var'
    pass=$TEST_USER_PASSWORD
  fi
  POD=$(kubectl get pod -l app=ldap -o name)
  kubectl exec "$POD" -- bash -c "export IFS_TEST_USER_PASSWORD=$pass && /usr/local/bin/ldap-sync-from-ifs-db.sh"
}

function initialiseTestEnvironment() {

    cd ${rootDir}

    if [[ ${quickTest} -eq 1 ]]
      then
        section "=> STARTING SELENIUM GRID and INJECTING ENVIRONMENT PARAMETERS"
        ./gradlew :robotTestsFilter --configure-on-demand

        echo "=> Waiting 5 seconds for the grid to be properly started"
        sleep 5
      else
        section "=> STARTING SELENIUM GRID, INJECTING ENVIRONMENT PARAMETERS, RESETTING DATABASE STATE"
        k8s_rebuild_db
        ./gradlew :robot-tests:deployHub :robot-tests:deployChrome :robotTestsFilter :ifs-data-layer:ifs-data-service:flywayMigrate -Pinitialise=true --configure-on-demand

        DATASERVICE_POD=$(kubectl get pod -l app=data-service -o jsonpath="{.items[0].metadata.name}")

#        ./gradlew :robot-tests:deployHub :robot-tests:deployChrome :robotTestsFilter -Pinitialise=true --configure-on-demand
        section "=> SYNCING SHIBBOLETH USERS"
#        ./gradlew :ifs-data-layer:ifs-data-service:syncShib 2>&1 >/dev/null
         k8s_sync_ldap
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
    if [[ ${ats} -eq 1 ]]
      then
        local includeAtsTags='--include AuthServiceTests'
      else
        local includeAtsTags='--exclude AuthServiceTests'
    fi
    if [[ ${zapTest} -eq 1 ]]
      then
        local includeZapTags='--include ZAPTests'
      else
        local includeZapTags='--exclude ZAPTests'
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
    -v local_imap:'host.docker.internal' \
    -v local_imap_port:8143 \
    $includeHappyPath \
    $includeBespokeTags \
    $excludeBespokeTags \
    $includeEuTags \
    $includeAtsTags \
    $includeZapTags \
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
    -v local_imap:'host.docker.internal' \
    -v local_imap_port:8143  \
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
webBase="host.docker.internal:8443"

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
ats=0
dryRun=0
openReports=1
zapTest=0

testDirectory='IFS_acceptance_tests/tests'
while getopts ":q :h :t :r :c :w :z :d: :x :R :B :I: :E: :a :o :p" opt ; do
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
        a)
          ats=1
        ;;
        p)
          zapTest=1
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
