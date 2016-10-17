ALTER TABLE process ADD COLUMN `internal_participant_id` bigint(20) DEFAULT NULL;

ALTER TABLE process ADD CONSTRAINT internal_participant_fk FOREIGN KEY (internal_participant_id) REFERENCES user(id);