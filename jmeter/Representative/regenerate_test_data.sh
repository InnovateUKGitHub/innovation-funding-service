#!/usr/bin/env bash

set -e

## reset the db
cd ../..
./gradlew initDB
cd -

./regenerate_test_data_and_apply_to_existing_db.sh

