#!/usr/bin/env bash

GLUSTER_1=10.0.0.31
GLUSTER_2=10.0.0.64

GLUSTER_1_HOST=ip-10-0-0-31.eu-west-2.compute.internal
GLUSTER_2_HOST=ip-10-0-0-64.eu-west-2.compute.internal

DISK_NUM=$1

# create gluster volume
ssh -A -t -t centos@${GLUSTER_1} sudo mkdir -p /data/brick2/gv${DISK_NUM}
ssh -A -t -t centos@${GLUSTER_2} sudo mkdir -p /data/brick2/gv${DISK_NUM}

ssh -A -t -t centos@${GLUSTER_1} sudo gluster volume create gv${DISK_NUM} replica 2 ${GLUSTER_1_HOST}:/data/brick2/gv${DISK_NUM} ${GLUSTER_2_HOST}:/data/brick2/gv${DISK_NUM}
ssh -A -t -t centos@${GLUSTER_1} sudo gluster volume start gv${DISK_NUM}


# create Openshift persistent volume
ssh cpm2 rm volume-tmp.yml

cp volume.yml volume-tmp.yml
sed -i.bak "s/fileupload-vol/fileupload-vol-${DISK_NUM}/" volume-tmp.yml
sed -i.bak "s/gv0/gv${DISK_NUM}/" volume-tmp.yml
cat volume-tmp.yml | ssh cpm2 "cat > volume-tmp.yml"
ssh cpm2 oc create -f volume-tmp.yml
rm volume-tmp.yml
