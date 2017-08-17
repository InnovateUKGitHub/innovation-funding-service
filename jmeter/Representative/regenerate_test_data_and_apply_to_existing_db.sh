#!/usr/bin/env bash

set -e

## regenerate users data
mysql ifs -hifs-database -uroot -ppassword --skip-column-names < regenerate_user_data.sql > user_data.csv 2> /dev/null

## regenerate invites data
mysql ifs -hifs-database -uroot -ppassword --skip-column-names < regenerate_invites_data.sql > invite_data.csv 2> /dev/null
