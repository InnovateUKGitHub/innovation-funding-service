#!/usr/bin/env bash

GLUSTER_2=10.0.0.64

FAILED_VOLUMES=$(ssh -A -t cpm2 \
    oc get pv | grep Failed | awk '{print $1}')

if [ -z "$FAILED_VOLUMES" ]; then
    echo "Nothing to delete"
    exit 0
fi


echo ${FAILED_VOLUMES} | xargs \
    ssh cpm2 oc delete pv

while read -r volume; do
    DISK_NUM=$(echo $volume | awk -F- '{print $NF}')
    ssh -t -t centos@${GLUSTER_2} sudo -u root rm -rf /data/brick2/gv${DISK_NUM}/*
    ./add-volume.sh $DISK_NUM
done <<< "$FAILED_VOLUMES"