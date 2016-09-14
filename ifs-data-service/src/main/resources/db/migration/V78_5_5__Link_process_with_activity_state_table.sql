INSERT INTO activity_state (activity_type, state) VALUES
  ('APPLICATION_ASSESSMENT', 'PENDING'),
  ('APPLICATION_ASSESSMENT', 'OPEN'),
  ('APPLICATION_ASSESSMENT', 'REJECTED'),
  ('APPLICATION_ASSESSMENT', 'READY_TO_SUBMIT'),
  ('APPLICATION_ASSESSMENT', 'SUBMITTED');

ALTER TABLE process ADD COLUMN activity_state_id bigint(20) NOT NULL;

UPDATE process p SET p.activity_state_id = (
  SELECT a.id FROM activity_state a
    WHERE (p.status = 'pending' AND a.state = 'PENDING')
       OR (p.status = 'open' AND a.state = 'OPEN')
       OR (p.status = 'rejected' AND a.state = 'REJECTED')
       OR (p.status = 'assessed' AND a.state = 'READY_TO_SUBMIT')
       OR (p.status = 'submitted' AND a.state = 'SUBMITTED'));

ALTER TABLE process DROP COLUMN status;

ALTER TABLE process ADD CONSTRAINT process_to_activity_state_fk FOREIGN KEY (activity_state_id) REFERENCES activity_state(id);