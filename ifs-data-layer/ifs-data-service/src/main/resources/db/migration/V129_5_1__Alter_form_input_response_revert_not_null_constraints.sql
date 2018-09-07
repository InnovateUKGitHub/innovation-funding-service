-- IFS-4304 - Revert the not null constraints added in IFS-3950.

ALTER TABLE form_input_response MODIFY form_input_id bigint(20) NULL;
ALTER TABLE form_input_response MODIFY application_id bigint(20) NULL;