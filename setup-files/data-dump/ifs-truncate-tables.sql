/*
-- Query: SELECT concat('TRUNCATE ', table_name, ';')
FROM information_schema.tables
WHERE table_schema = 'ifs-data'
-- Date: 2015-08-11 14:24
*/
SET foreign_key_checks = 0;
SET SQL_SAVE_UPDATES=0;
TRUNCATE application;
TRUNCATE application_finance;
TRUNCATE application_status;
TRUNCATE capital_usage;
TRUNCATE competition;
TRUNCATE cost;
TRUNCATE cost_category;
TRUNCATE cost_field;
TRUNCATE cost_value;
TRUNCATE labour;
TRUNCATE materials;
TRUNCATE organisation;
TRUNCATE other_cost;
TRUNCATE question;
TRUNCATE question_type;
TRUNCATE response;
TRUNCATE role;
TRUNCATE section;
TRUNCATE subcontractor;
TRUNCATE travel_cost;
TRUNCATE competition_applications;
TRUNCATE process;
TRUNCATE response_assessment_linker;
TRUNCATE assessment_assessments_linker;
TRUNCATE assessment_response_assessments;

TRUNCATE user;
TRUNCATE user_role;
TRUNCATE user_application_role;
TRUNCATE assessment;
TRUNCATE assessment_assessments;
TRUNCATE assessment_process;
TRUNCATE hibernate_sequences;
TRUNCATE question_status;
TRUNCATE response_assessment;
TRUNCATE process_event;
TRUNCATE assessment_process_response_assessments;
TRUNCATE process_role;
TRUNCATE process_type;
TRUNCATE process_value;

SET SQL_SAVE_UPDATES=1;




