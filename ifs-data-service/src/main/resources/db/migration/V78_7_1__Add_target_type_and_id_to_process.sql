ALTER TABLE process ADD COLUMN target_id BIGINT(20) NULL;

UPDATE process p SET p.target_id = (
  SELECT a.id FROM process_role pr
    JOIN application a ON a.id = pr.application_id
    WHERE pr.id = p.process_role);