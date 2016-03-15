#!/usr/bin/env bash
bash _linux-purge-and-start.sh
bash _linux-edit-hosts.sh
cd ../common
sleep 10
bash reset-users-from-database.sh









