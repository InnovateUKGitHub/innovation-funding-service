#!/usr/bin/env bash

set -e

## reset the db
cd ../..
./gradlew initDB
cd -

## clear out any existing csvs
sudo find /var/lib/docker/ -name 'users-csv.csv' | xargs sudo rm -f
sudo find /var/lib/docker/ -name 'invites-csv.csv' | xargs sudo rm -f

## regenerate users data
mysql ifs -hifs-database -uroot -ppassword < regenerate_user_data.sql

## regenerate invites data
mysql ifs -hifs-database -uroot -ppassword < regenerate_invites_data.sql

## collect the files and replace the existing csvs with these new ones
userscsv=$(sudo find /var/lib/docker/ -name 'users-csv.csv')
invitescsv=$(sudo find /var/lib/docker/ -name 'invites-csv.csv')
sudo cp $userscsv user_data.csv
sudo cp $invitescsv invite_data.csv

## clean up the format
sed -i 's/\\,/,/g' user_data.csv
sed -i 's/\\,/,/g' invite_data.csv

## and finally, clean up the database again
cd ../..
./gradlew initDB
