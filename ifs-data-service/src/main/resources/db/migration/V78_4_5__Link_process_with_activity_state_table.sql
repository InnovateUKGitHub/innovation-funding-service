ALTER TABLE process ADD COLUMN activity_state_id bigint(20) NOT NULL;

INSERT INTO activity_state (activity_type, state)
  SELECT 'APPLICATION_ASSESSMENT', CASE p.status
      WHEN 'pending' THEN 'PENDING'
      WHEN 'rejected' THEN 'REJECTED'
      WHEN 'open' THEN 'OPEN'
      WHEN 'assessed' THEN 'READY_TO_SUBMIT'
      WHEN 'submitted' THEN 'SUBMITTED'
    END
    FROM process p;

UPDATE process p SET p.activity_state_id = p.id;

ALTER TABLE process DROP COLUMN status;

ALTER TABLE process ADD CONSTRAINT process_to_activity_state_fk FOREIGN KEY (activity_state_id) REFERENCES activity_state(id);