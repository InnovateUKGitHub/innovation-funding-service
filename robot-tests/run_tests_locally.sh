#!/bin/bash

sudo echo "Thanks for entering sudo!"

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
    wait
    cd ${dataTomcatBinPath}
    ./shutdown.sh
    wait
    echo "*********KILLING ALL IFS TOMCAT PROCESSES*********"
    ps -ef | grep ${dataTomcatBinPath} | grep 'Bootstrap start' | awk '{print $2}' | xargs -i kill -9 {}
    ps -ef | grep ${webTomcatBinPath} | grep 'Bootstrap start' | awk '{print $2}' | xargs -i kill -9 {}
    echo "********UNDEPLOYING THE APPLICATION********"
    cd ${dataWebappsPath}
    rm -rf ROOT ROOT.war
    cd ${webWebappsPath}
    rm -rf ROOT ROOT.war
}

function resetDB {
    echo "********DROP THE DATABASE********"
    cd ${scriptDir}
    `mysql -u${mysqlUser} -p${mysqlPassword} -e"DROP DATABASE ifs"`
    `mysql -u${mysqlUser} -p${mysqlPassword} -e"CREATE DATABASE ifs CHARACTER SET utf8"`
    cd ../ifs-data-service
    ./gradlew clean processResources flywayClean flywayMigrate
}

function clearDownFileRepository {
    echo "***********Deleting any uploaded files***************"
    echo "storedFileFolder:		${storedFileFolder}"
    rm -rf ${storedFileFolder}

    echo "***********Deleting any holding for scan files***************"
    echo "virusScanHoldingFolder:	${virusScanHoldingFolder}"
    rm -rf ${virusScanHoldingFolder}

    echo "***********Deleting any quarantined files***************"
    echo "virusScanQuarantinedFolder:	${virusScanQuarantinedFolder}"
    rm -rf ${virusScanQuarantinedFolder}

    echo "***********Deleting any scanned files***************"
    echo "virusScanScannedFolder:	${virusScanScannedFolder}"
    rm -rf ${virusScanScannedFolder}
}

function addTestFiles {
    echo "***********Adding test files***************"
    echo "***********Making the quarantined directory ***************"
    mkdir -p ${virusScanQuarantinedFolder}
    echo "***********Adding pretend quarantined file ***************"
    cp ${uploadFileDir}/8 ${virusScanQuarantinedFolder}/8
}


function buildAndDeploy {
    echo "********BUILD AND DEPLOY THE APPLICATION********"
    cd ${dataServiceCodeDir}
    ## Before we start the build we need to have an webtest test build environment
    echo "********SWAPPING IN THE WEBTEST BUILD PROPERTIES********"
    sed 's/ext\.ifsFlywayLocations.*/ext\.ifsFlywayLocations="db\/migration,db\/setup,db\/webtest"/' dev-build.gradle > webtest.gradle.tmp
    mv dev-build.gradle dev-build.gradle.tmp
    mv webtest.gradle.tmp dev-build.gradle
    ./gradlew clean deployToTomcat
    ./gradlew flywayMigrate
    ## Replace the webtest build environment with the one we had before.
    echo "********SWAPPING BACK THE ORIGINAL BUILD PROPERTIES********"
    mv dev-build.gradle.tmp dev-build.gradle

    cd ${webServiceCodeDir}
    ./gradlew clean deployToTomcat

}

function resetLDAP {
    cd ${shibbolethScriptsPath}
    ./reset-shibboleth-users.sh
}

function startServers {
    echo "********START SHIBBOLETH***********"
    cd ${shibbolethScriptsPath}
    ./startup-shibboleth.sh
    wait
    cd ../shibboleth/ui
    # ./deploy-ui.sh
    echo "********START THE DATA SERVER********"
    cd ${dataTomcatBinPath}

    if [ "$startServersInDebugMode" ]; then
      export JPDA_ADDRESS=8000
      export JPDA_TRANSPORT=dt_socket
      ./catalina.sh jpda start
    else
      ./startup.sh
    fi


    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
    touch ${dataLogFilePath}
    tail -F -n0 ${dataLogFilePath} | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
    echo "********START THE WEB SERVER********"
    touch ${webTomcatBinPath}
    cd ${webTomcatBinPath}

    if [ "$startServersInDebugMode" ]; then
      export JPDA_ADDRESS=8001
      export JPDA_TRANSPORT=dt_socket
      ./catalina.sh jpda start
    else
      ./startup.sh
    fi

    echo "**********WAIT FOR SUCCESSFUL DEPLOYMENT OF THE APPLICATION**********"
    tail -F -n0 ${webLogFilePath} | while read logLine
    do
      [[ "${logLine}" == *"Deployment of web application archive"* ]] && pkill -P $$ tail
    done
}

function resetDocker {
    echo "********RESETTING DOCKERIZED ENVIRONMENT******************"
    cd ${scriptDir}
    cd ../setup-files/scripts/docker
    docker-machine stop
    wait
    docker-machine start
    wait
    ./migrate.sh
    wait
    ./startup.sh
    wait
    ./deploy.sh all
    wait
    cd -
}

function runTests {
    echo "**********RUN THE WEB TESTS**********"
    cd ${scriptDir}

    if [ "$localMailSendingImplemented" ]
    then
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb -v test_mailbox_one:$testMailboxOne -v test_mailbox_two:$testMailboxTwo -v test_mailbox_one_password:$testMailboxOnePassword -v test_mailbox_two_password:$testMailboxTwoPassword -v sender:dev-dwatson-liferay-portal@hiveit.co.uk -v database_user:$mysqlUser -v database_password:$mysqlPassword -v database_host:localhost --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
    else
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb -v database_user:$mysqlUser -v database_password:$mysqlPassword -v database_host:localhost --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS $testDirectory
    fi
}


function runHappyPathTests {
    echo "*********RUN THE HAPPY PATH TESTS ONLY*********"
    cd ${scriptDir}

    if [ "$localMailSendingImplemented" ]
    then
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb -v test_mailbox_one:$testMailboxOne -v test_mailbox_two:$testMailboxTwo -v test_mailbox_one_password:$testMailboxOnePassword -v test_mailbox_two_password:$testMailboxTwoPassword -v sender:dev-dwatson-liferay-portal@hiveit.co.uk -v database_user:$mysqlUser -v database_password:$mysqlPassword -v database_host:localhost --include HappyPath --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
    else
        pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$webBase -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb -v database_user:$mysqlUser -v database_password:$mysqlPassword -v database_host:localhost --include HappyPath --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS $testDirectory
    fi
}

function runTestsRemotely {
    echo "***********RUNNING AGAINST THE IFS DEV SERVER...**********"
    cd ${scriptDir}
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_AUTH:ifs:Fund1ng -v SERVER_BASE:ifs.dev.innovateuk.org -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:'YES' -v LOCAL_MAIL_SENDING_IMPLEMENTED:'YES' -v UPLOAD_FOLDER:$uploadFileDir -v RUNNING_ON_DEV:'YES' --exclude MySQL --exclude Failing --exclude Pending --exclude FailingForDev --name IFS $testDirectory
}


function runSmokeTests {
    echo "***************RUNNING SMOKE TEST. PLEASE WATCH YOUR SCREEN!*****************"
    cd ${scriptDir}
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$smokeServer -v PROTOCOL:'https://' -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:NO -v test_mailbox_one:$testMailboxOne -v test_mailbox_two:$testMailboxTwo -v test_mailbox_one_password:$testMailboxOnePassword -v test_mailbox_two_password:$testMailboxTwoPassword -v sender:noresponse@innovateuk.gov.uk -v test_title:'IFS smoke test' -v application_name:'IFS smoke test' -v unique_email_number:$dateFormat -v smoke_test:1 -v submit_test_email:'${test_mailbox_one}+${unique_email_number}@gmail.com' --include SmokeTest --exclude MySQL --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
}

function runTestsAgainstDocker {
    echo "**************RUNNING TESTS AGAINST DOCKER***********************"
    cd ${scriptDir}
    resetDocker
    pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:$browser -v SERVER_BASE:$webBase -v PROTOCOL:"https://" -v POSTCODE_LOOKUP_IMPLEMENTED:$postcodeLookupImplemented -v UPLOAD_FOLDER:$uploadFileDir -v DOWNLOAD_FOLDER:download_files -v VIRTUAL_DISPLAY:$useXvfb -v docker:1 --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --name IFS $testDirectory
}

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
baseFileStorage=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsFileStorageLocation'  | cut -d "=" -f2 | sed 's/"//g'`
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
postcodeLookupKey=`sed '/^\#/d' dev-build.gradle | grep 'ext.postcodeLookupKey'  | cut -d "=" -f2 | sed 's/"//g'`
echo "Postcode Lookup: 		${postcodeLookupKey}"
if [ "$postcodeLookupKey" = '' ]
then
    echo "Postcode lookup not implemented"
    postcodeLookupImplemented='NO'
else
    echo "Postcode lookup implemented. The tests will expect proper data from the SuT."
    postcodeLookUpImplemented='YES'
fi
sendMailLocally=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsSendMailLocally'  | cut -d "=" -f2 | sed 's/"//g'`
if [ $sendMailLocally = 'false' ]
then
    echo "Sending mail locally not implemented"
    unset localMailSendingImplemented
else
    echo "Sending mail locally is implemented. The tests will expect emails to be sent out to all whitelisted recipients. Please take care not to spam anyone!"
    localMailSendingImplemented='YES'
fi
testMailboxOneExists=`sed '/^\#/d' dev-build.gradle | grep 'ifsTestMailOne'`
if [ "$testMailboxOneExists" ]
then
    echo "It looks like you've configured your own test mailboxes, so using those. If you see connection errors, remember to allow less secure access!"
    testMailboxOne=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsTestMailOne'  | cut -d "=" -f2 | sed 's/"//g' | cut -d "@" -f1`
    testMailboxTwo=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsTestMailTwo'  | cut -d "=" -f2 | sed 's/"//g' | cut -d "@" -f1`
    testMailboxOnePassword=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsTestMailPasswordOne'  | cut -d "=" -f2 | sed 's/"//g'`
    testMailboxTwoPassword=`sed '/^\#/d' dev-build.gradle | grep 'ext.ifsTestMailPasswordTwo'  | cut -d "=" -f2 | sed 's/"//g'`
else
    echo "We're going to use the normal test mailboxes, please be aware that you may get email collisions if someone else is running at the same time!"
    testMailboxOne='worth.email.test'
    testMailboxTwo='worth.email.test.two'
    testMailboxOnePassword='testtest1'
    testMailboxTwoPassword='testtest1'
fi
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
webBase="ifs-local-dev"
echo "webBase:           ${webBase}"


unset opt
unset quickTest
unset testScrub
unset happyPath
useXvfb=true
unset remoteRun
unset startServersInDebugMode
unset testMailboxOneExists
unset secretMode

browser="GoogleChrome"


testDirectory='IFS_acceptance_tests/tests/*'
while getopts ":q :t :h :p :r :d: :D :x :f :s :S:" opt ; do
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
        f)
         browser="Firefox"
        ;;
        S)
         smokeTest=1
         smokeServer="$OPTARG"
        ;;
	s)
        secretMode=1
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
    resetLDAP
    clearDownFileRepository
    addTestFiles
    runTests
elif [ "$testScrub" ]
then
    echo "using testScrub mode: this will do all the dirty work but omit the tests" >&2
    stopServers
    resetDB
    clearDownFileRepository
    addTestFiles
    buildAndDeploy
    startServers
elif [ "$happyPath" ]
then 
    echo "using happyPath mode: this will run a pared down set of tests as a sanity check for developers pre-commit" >&2
    stopServers
    resetDB
    clearDownFileRepository
    addTestFiles
    buildAndDeploy
    startServers
    runHappyPathTests
elif [ "$happyPathTestOnly" ]
then
    echo "run test only"
    runHappyPathTests
elif [ "$remoteRun" ]
then 
    echo "Pointing the tests at the ifs dev server - note that some tests may fail if you haven't scrubbed the dev server's db" >&2
    runTestsRemotely
elif [ "$smokeTest" ]
then 
    echo "Running smoke test against chosen environment"
    runSmokeTests
elif [ "$secretMode" ]
then
    echo "secret mode activated! make sure your docker environment is ready to run tests against"
    runTestsAgainstDocker
else
    echo "using quickTest:   FALSE" >&2
    stopServers
    resetDB
    clearDownFileRepository
    addTestFiles
    buildAndDeploy
    startServers
    runTests
fi
