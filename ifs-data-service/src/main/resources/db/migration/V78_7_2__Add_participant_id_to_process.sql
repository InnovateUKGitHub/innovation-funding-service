ALTER TABLE process ADD COLUMN participant_id BIGINT(20) NULL;

UPDATE process p SET p.participant_id = p.process_role;