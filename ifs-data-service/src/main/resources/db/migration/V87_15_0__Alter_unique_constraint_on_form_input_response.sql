ALTER TABLE `form_input_response` DROP INDEX unique_application_form_input;

ALTER IGNORE TABLE `form_input_response` ADD UNIQUE KEY unique_application_form_input(application_id, form_input_id, updated_by_id);