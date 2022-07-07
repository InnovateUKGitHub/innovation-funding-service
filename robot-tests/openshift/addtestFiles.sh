#!/bin/bash

SVC_ACCOUNT_CLAUSE=$1

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

    DATA_SERVICE_POD=$(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep ^data-service | awk '{ print $1 }')

    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} rm -rf ${storedFileFolder}

    echo "***********Deleting any holding for scan files***************"
    echo "virusScanHoldingFolder: ${virusScanHoldingFolder}"
    oc  ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} rm -rf ${virusScanHoldingFolder}

    echo "***********Deleting any quarantined files***************"
    echo "virusScanQuarantinedFolder: ${virusScanQuarantinedFolder}"
    oc  ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} rm -rf ${virusScanQuarantinedFolder}

    echo "***********Deleting any scanned files***************"
    echo "virusScanScannedFolder: ${virusScanScannedFolder}"
    oc  ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} rm -rf ${virusScanScannedFolder}
}

function addTestFiles() {
    section "=> RESETTING FILE STORAGE STATE"

    DATA_SERVICE_POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep ^data-service | awk '{ print $1 }')

    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} apt-get update 
    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} apt-get install -yq rsync mysql-client

    clearDownFileRepository
    echo "***********Adding test files***************"
    oc ${SVC_ACCOUNT_CLAUSE} rsync --include=testing.pdf ${uploadFileDir}/ ${DATA_SERVICE_POD}:/tmp/

    echo "***********Making the quarantined directory ***************"
    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} cp /tmp/testing.pdf ${virusScanQuarantinedFolder}/8

    echo "***********Adding standard file upload location ***********"
    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} mkdir -p ${storedFileFolder}

    oc ${SVC_ACCOUNT_CLAUSE} rsync --include=fileForEachDBEntry.sh ${scriptDir}/ ${DATA_SERVICE_POD}:
    oc ${SVC_ACCOUNT_CLAUSE} rsh ${DATA_SERVICE_POD} sh fileForEachDBEntry.sh ${storedFileFolder}
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

uploadFileDir="${scriptDir}/../upload_files"
baseFileStorage="/tmp"
storedFileFolder="${baseFileStorage}"
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

addTestFiles
