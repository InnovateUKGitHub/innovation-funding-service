#!/bin/bash
./generate_migration_scripts.sh
./generate_setup_scripts.sh
./replace_old_scripts_with_new.sh
./generate_schema_version_statement_for_uat_and_live.sh
./dry_run_live.sh
./new_patches_not_on_live.sh
./git_revert_flyway_scripts.sh



