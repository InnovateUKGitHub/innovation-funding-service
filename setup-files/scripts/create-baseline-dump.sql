# A script to generate a dump from a database without any of the reference data tables in it


mysql -h ifs-database ifs -uroot -ppassword -e 'delete from comp_admin_emails where id <= 10;'
mysql -h ifs-database ifs -uroot -ppassword -e 'delete from project_finance_emails where id <= 11;'
mysql -h ifs-database ifs -uroot -ppassword -e 'delete from user_role where exists (select 1 from user u where u.id = user_id and u.system_user = 1);'
mysql -h ifs-database ifs -uroot -ppassword -e 'SET foreign_key_checks = 0; delete from user where system_user = 1; SET foreign_key_checks = 1;'


mysqldump -h ifs-database ifs -uroot -ppassword --no-create-info --extended-insert=false --ignore-table=ifs.schema_version --ignore-table=ifs.activity_state --ignore-table=ifs.finance_row_meta_field --ignore-table=ifs.address_type --ignore-table=ifs.application_status --ignore-table=ifs.category --ignore-table=ifs.form_validator --ignore-table=ifs.form_input_type --ignore-table=ifs.organisation_type --ignore-table=ifs.participant_status --ignore-table=ifs.project_role --ignore-table=ifs.role --ignore-table=ifs.competition_type --ignore-table=ifs.academic --ignore-table=ifs.contract --ignore-table=ifs.ethnicity --ignore-table=ifs.rejection_reason > ../../ifs-data-service/src/main/resources/db/webtest/V81_4_2__Base_webtest_data.sql
