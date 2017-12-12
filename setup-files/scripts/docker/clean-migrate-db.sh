#!/bin/bash
# R Popple: Clean down the database and run all the flyway migrations against it.
echo "Cleaning and migrating the database"
flyway clean
flyway migrate
flyway info
flyway validate
