#!/bin/bash
echo -e "Re-mounting shm and tmp\n\n"
sudo umount -l /dev/shm
sudo umount -l /tmp
sudo mount -t tmpfs -o size=1280m tmpfs /dev/shm
sudo mount -t tmpfs -o size=768m tmpfs /tmp
echo -e "\n\n"
cat /proc/mounts
echo -e "\n\n"
df -h
echo -e "\n\nStarting node\n\n"

. /opt/bin/entry_point.sh