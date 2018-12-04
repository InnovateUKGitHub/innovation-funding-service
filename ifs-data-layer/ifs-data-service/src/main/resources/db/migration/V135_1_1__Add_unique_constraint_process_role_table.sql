ALTER TABLE process_role
ADD CONSTRAINT `UC_process_role_unique_row` UNIQUE (`application_id`, `role_id`, `user_Id`, `organisation_id`);