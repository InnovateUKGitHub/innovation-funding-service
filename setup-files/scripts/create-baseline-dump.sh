#!/usr/bin/env bash
cd "$(dirname "$0")"
# A script to generate a dump from a database without any of the reference data tables in it

baselineversion="${1?please specify the new baseline version (e.g. V100_11_)}"

echo "database host is  ${2}"

echo "creating baseline for version ${baselineversion}"
mysql -h ${2} ifs -uroot -ppassword -e 'delete from user_role where exists (select 1 from user u where u.id = user_id and u.system_user = 1);'
mysql -h ${2} ifs -uroot -ppassword -e 'SET foreign_key_checks = 0; delete from user where system_user = 1; SET foreign_key_checks = 1;'


mysqldump -h ${2} ifs -uroot -ppassword --no-create-info --ignore-table=ifs.schema_version --ignore-table=ifs.flyway_schema_history --ignore-table=ifs.activity_state --ignore-table=ifs.finance_row_meta_field --ignore-table=ifs.address_type --ignore-table=ifs.application_status --ignore-table=ifs.category --ignore-table=ifs.form_validator --ignore-table=ifs.form_input_type --ignore-table=ifs.organisation_type --ignore-table=ifs.participant_status --ignore-table=ifs.project_role --ignore-table=ifs.role --ignore-table=ifs.competition_type --ignore-table=ifs.academic --ignore-table=ifs.agreement --ignore-table=ifs.ethnicity --ignore-table=ifs.rejection_reason --ignore-table=ifs.assessor_count_option --ignore-table=ifs.grant_claim_maximum --ignore-table=ifs.organisation_size --ignore-table=ifs.terms_and_conditions --ignore-table=ifs.eu_action_type --ignore-table=ifs.eu_grant_transfer > ../../ifs-data-layer/ifs-data-service/src/main/resources/db/webtest/${baselineversion}8__Base_webtest_data.sql