#!/bin/bash
# R Popple: Clean down the database and run all the flyway migrations against it.
echo "Cleaning and migrating the database"

cat /flyway/flyway.conf

echo "end of file"
flyway clean
flyway migrate
flyway info
flyway validate
