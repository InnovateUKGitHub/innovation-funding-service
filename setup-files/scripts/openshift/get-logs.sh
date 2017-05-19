#!/usr/bin/env bash

# Copies all the container logs for a given project into plain text files on the local machine.
# Updates every five minutes and retains the logs for 30 days.
# Must be triggered by a cron job that runs at the same frequency.
# e.g. crontab:
#   */5 * * * * /home/centos/oclogs/bin/get-logs.sh -c /home/centos/oclogs/bin/oc 2>&1

OC="oc"
DIRECTORY="/var/log/containers"
PROJECT="production"
TOKEN=""
INTERVAL="5m"
MAX_DAYS=30

unset opt
while getopts ":c: :p: :d: :t: :i: :m:" opt ; do
    case $opt in
        c)
            OC="$OPTARG"
            ;;
        p)
            PROJECT="$OPTARG"
            ;;
        d)
            DIRECTORY="$OPTARG"
            ;;
        t)
            TOKEN="$OPTARG"
            ;;
        t)
            INTERVAL="$OPTARG"
            ;;
        t)
            MAX_DAYS=$OPTARG
            ;;
        \?)
            echo '-c oc_command    : path to the oc executable'
            echo '-p project       : project name (production, demo, uat, sysint)'
            echo '-d log_directory : root path in which to store the logs'
            echo '-t access_token  : access token for the OpenShift service account'
            echo '-i interval      : fetch logs newer than this interval; cron job must run with the same interval'
            echo '-m max_days      : delete logs older than this number of days'
            exit 0
            ;;
    esac
done

datestr=$(date +%Y-%m-%d)
logdir=$DIRECTORY/$PROJECT/$datestr
mkdir -p $logdir

if [ -z "$TOKEN" ]; then SVC_ACCOUNT_TOKEN=$($OC whoami -t); else SVC_ACCOUNT_TOKEN=${TOKEN}; fi

SVC_ACCOUNT_CLAUSE="--namespace=$PROJECT --token=$SVC_ACCOUNT_TOKEN --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"

$OC project $PROJECT $SVC_ACCOUNT_CLAUSE || exit 1

pods=$( $OC get pods $SVC_ACCOUNT_CLAUSE | awk 'NR>1 { print $1 }' )

for pod in $pods; do
    $OC logs $pod --since="$INTERVAL" $SVC_ACCOUNT_CLAUSE >> $logdir/$pod.log
done

# clean up old directories
find $DIRECTORY/$PROJECT/* -type d -ctime +$MAX_DAYS | xargs rm -rf
