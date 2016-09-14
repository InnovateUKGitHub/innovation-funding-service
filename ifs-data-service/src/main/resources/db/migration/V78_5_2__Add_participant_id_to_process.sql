ALTER TABLE process ADD COLUMN participant_id BIGINT(20) NOT NULL;

UPDATE process p SET p.participant_id = p.process_role;