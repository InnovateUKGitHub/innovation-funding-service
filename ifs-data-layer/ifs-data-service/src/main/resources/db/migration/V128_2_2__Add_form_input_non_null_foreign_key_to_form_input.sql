-- IFS-3950 - adding missing constraints to the form_input_response table to improve data integrity

ALTER TABLE form_input_response MODIFY form_input_id bigint(20) NOT NULL;
ALTER TABLE form_input_response MODIFY application_id bigint(20) NOT NULL;