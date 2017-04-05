-- add a new status enum to replace the existing application_status_id
ALTER TABLE application
  ADD COLUMN status ENUM('CREATED', 'SUBMITTED', 'APPROVED', 'REJECTED', 'OPEN');

-- set the existing states
UPDATE application SET status = (SELECT upper(name) FROM application_status st WHERE st.id = application_status_id);

-- make the status column not null
ALTER TABLE application MODIFY status ENUM('CREATED', 'SUBMITTED', 'APPROVED', 'REJECTED', 'OPEN') NOT NULL;

-- drop the old application_status_id column
ALTER TABLE application
  DROP FOREIGN KEY FK_mdoygpekookkkntrvgy67jjlb,
  DROP COLUMN application_status_id;

-- drop the application_status table
DROP TABLE application_status;