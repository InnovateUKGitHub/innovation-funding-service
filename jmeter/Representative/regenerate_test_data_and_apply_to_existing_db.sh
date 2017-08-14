#!/usr/bin/env bash

set -e

## regenerate users data
mysql ifs -hifs-database -uroot -ppassword < regenerate_user_data.sql | tail -n +2 > user_data.csv 2> /dev/null

## regenerate invites data
mysql ifs -hifs-database -uroot -ppassword < regenerate_invites_data.sql | tail -n +2 > invite_data.csv 2> /dev/null
