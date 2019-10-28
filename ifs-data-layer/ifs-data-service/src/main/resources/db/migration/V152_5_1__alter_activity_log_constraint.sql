ALTER TABLE activity_log
DROP FOREIGN KEY fk_activity_log_thread_id;

ALTER TABLE activity_log
ADD CONSTRAINT fk_activity_log_thread_id
    FOREIGN KEY (thread_id) REFERENCES thread(id) ON DELETE SET NULL;

ALTER TABLE bank_details
DROP FOREIGN KEY bank_details_to_organisation_address_fk;

ALTER TABLE bank_details
ADD CONSTRAINT bank_details_to_organisation_address_fk
    FOREIGN KEY (organisation_address_id) REFERENCES organisation_address (id) ON DELETE CASCADE;