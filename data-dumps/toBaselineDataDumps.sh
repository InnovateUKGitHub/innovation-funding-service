#!/bin/bash
#1) Create a database dump from the latest flyway
cd ../ifs-data-service/
./gradlew flywayClean flywayMigrate;
cd ../data-dumps/
mysql --database=ifs -uifs -pifs -B -N -e "SHOW TABLES"  | awk '{print "ALTER TABLE", $1, "CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;"}' | mysql -uifs -pifs --database=ifs
mysqldump --no-data --add-drop-table -uifs -pifs ifs --ignore-table=ifs.schema_version --default-character-set=utf8 > originalSchemaOnly.sql
mysqldump --no-create-info --extended-insert=false -uifs -pifs ifs application_status cost_field form_input_type form_input_validator form_validator role organisation_type --default-character-set=utf8 > originalReferenceDataOnly.sql
mysqldump --no-create-info --extended-insert=false -uifs -pifs ifs --ignore-table=ifs.organisation_type --ignore-table=ifs.application_status --ignore-table=ifs.cost_field --ignore-table=ifs.form_input_type --ignore-table=ifs.form_input_validator --ignore-table=ifs.form_validator --ignore-table=ifs.role --ignore-table=ifs.schema_version --default-character-set=utf8 > originalTestDataOnly.sql
#2) Baseline the scripts
cd ../ifs-data-service/src/main/resources/db/migration/
rm -rf *
cp ../../../../../../data-dumps/originalSchemaOnly.sql V1__BaseVersion.sql
cp ../../../../../../data-dumps/originalReferenceDataOnly.sql V2__ReferenceData.sql
#3) Copy the test data across
cd ../integration
touch V1_1__IntegrationInitial.sql
echo "-- Initial empty integration data script to help ensure that we cannot accidentially run this scripts on another environment" > V1_1__IntegrationInitial.sql
cp ../../../../../../data-dumps/originalTestDataOnly.sql V2_1__TestDataBase.sql
cd ../development
touch V1_1__DevelopmentInitial.sql
echo "-- Initial empty development data script to help ensure that we cannot accidentially run this scripts on another environment" > V1_1__DevelopmentInitial.sql
cp ../../../../../../data-dumps/originalTestDataOnly.sql V2_1__TestDataBase.sql
cd ../acceptance
touch V1_1__AcceptanceInitial.sql
echo "-- Initial empty acceptance data script to help ensure that we cannot accidentially run this scripts on another environment" > V1_1__AcceptanceInitial.sql
cp ../../../../../../data-dumps/originalTestDataOnly.sql V2_1__TestDataBase.sql
touch V2_2__RemoveCosts.sql
echo "DELETE FROM cost_value WHERE cost_id = 13;" >  V2_2__RemoveCosts.sql
echo "DELETE FROM cost WHERE id IN(2,4,12,13,19,20);" >> V2_2__RemoveCosts.sql
cd ../../../../../../
#4) Clear the databases
mysql -uifs -pifs -e"DROP DATABASE ifs"
mysql -uifs -pifs -e"DROP DATABASE ifs_test"
mysql -uifs -pifs -e"CREATE DATABASE ifs_test CHARACTER SET utf8"
mysql -uifs -pifs -e"CREATE DATABASE ifs CHARACTER SET utf8"

#========
#=REVERT=
#========
#cd ifs-data-service
#git checkout src/main/resources/db/migration/V10_1__Cleanup_question_status_table.sql src/main/resources/db/migration/V10_2__UpdateWordCount.sql src/main/resources/db/migration/V1__Base_version.sql src/main/resources/db/migration/V2_10__MakeOriginalFirstPriorityQuestionsAssessable.sql src/main/resources/db/migration/V2_1__Add_Form_Input_Tables.sql src/main/resources/db/migration/V2_2__Migrate_Questions_To_Form_Inputs.sql src/main/resources/db/migration/V2_3__Migrate_Responses_To_Form_Input_Responses.sql src/main/resources/db/migration/V2_4__RemoveChildQuestions.sql src/main/resources/db/migration/V2_5__RemoveQuestionTypes.sql src/main/resources/db/migration/V2_6__DropChildQuestions.sql src/main/resources/db/migration/V2_7__RemoveValuesFromQuestionsAndResponses.sql src/main/resources/db/migration/V2_8__AddIncludedInApplicationSummaryColumn.sql src/main/resources/db/migration/V2_9__AddDescriptionColumnToFormInput.sql src/main/resources/db/migration/V3_1__Add_Form_Input_Validator_Tables.sql src/main/resources/db/migration/V3_2__Add_Validators_For_Textarea.sql src/main/resources/db/migration/V3_3__Refactor_Validator_Link_Table.sql src/main/resources/db/migration/V3_4__Add_Textarea_Validations.sql src/main/resources/db/migration/V3_5__SetQuestionNameForCustomFinanceSummaryCompleteHandling.sql src/main/resources/db/migration/V4_1__Add_finances_default_values.sql src/main/resources/db/migration/V4_2__Add_finances_cost_fields.sql src/main/resources/db/migration/V4_3__Add_finances_default_values.sql src/main/resources/db/migration/V5_1__Update_section_names.sql src/main/resources/db/migration/V5_2__Update_competition_startdate.sql src/main/resources/db/migration/V5_3__AddWordCountValidatorToFormInputs.sql src/main/resources/db/migration/V5_4__AddShortTitlesForQuestions.sql src/main/resources/db/migration/V5_5__UpdateQuestionsAssignmentToSections.sql src/main/resources/db/migration/V6_1__AddDirectUserOrganisationRelationship.sql src/main/resources/db/migration/V6_2__AddColumnsToUser.sql src/main/resources/db/migration/V6_3__UpdateShortTitlesWithoutNumbers.sql src/main/resources/db/migration/V6_4__UpdateQuestionsAssignmentToSections.sql src/main/resources/db/migration/V6_5__GroupQuestionsBySection.sql src/main/resources/db/migration/V7_1__Create_organisation_address_table.sql src/main/resources/db/migration/V7_2__Add_address_attribute.sql src/main/resources/db/migration/V7_3__Assign_questions_correctly.sql src/main/resources/db/migration/V7_4__Remove_question_numbers.sql src/main/resources/db/migration/V7_5__Update_Grant_Percentage_Question.sql src/main/resources/db/migration/V7_6__Update_Questions_Priority.sql src/main/resources/db/migration/V8_1__Add_FileEntry_Table.sql src/main/resources/db/migration/V8_2__Add_FileEntryId_Column_To_FormInputResponse.sql src/main/resources/db/migration/V8_3__Update_Other_Funding.sql src/main/resources/db/migration/V8_5__UpdateCompetitionDeadline.sql src/main/resources/db/migration/V9_1__RefactorProcesses.sql src/main/resources/db/migration/V9_2__Update_question_mark_as_done.sql src/main/resources/db/migration/V9_3__Update_question_mark_as_done.sql
#git checkout src/main/resources/db/migration/SCRIPTS_THAT_WILL_BE_RUN_ON_PRODUCTION src/main/resources/db/migration/V10_3__Created_organisation_type_table.sql
#rm src/main/resources/db/migration/V1__BaseVersion.sql
#rm src/main/resources/db/migration/V2__ReferenceData.sql
#rm src/main/resources/db/acceptance/V2_2__RemoveCosts.sql src/main/resources/db/acceptance/V2_1__TestDataBase.sql src/main/resources/db/development/V2_1__TestDataBase.sql src/main/resources/db/integration/V2_1__TestDataBase.sql src/main/resources/db/migration/V11_1__invite_tables.sql
#cd ../data-dumps
#rm originalReferenceDataOnly.sql originalSchemaOnly.sql originalTestDataOnly.sql
#cd ..
#mysql -uifs -pifs -e"DROP DATABASE ifs"
#mysql -uifs -pifs -e"DROP DATABASE ifs_test"
#mysql -uifs -pifs -e"CREATE DATABASE ifs_test CHARACTER SET utf8"
#mysql -uifs -pifs -e"CREATE DATABASE ifs CHARACTER SET utf8"







