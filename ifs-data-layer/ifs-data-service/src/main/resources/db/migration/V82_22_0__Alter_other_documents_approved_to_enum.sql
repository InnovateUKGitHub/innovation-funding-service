ALTER TABLE project ADD COLUMN other_documents_approved_temp ENUM('UNSET', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'UNSET' AFTER documents_submitted_date;

UPDATE project AS p, (select id from project where other_documents_approved = 0) AS a
SET p.other_documents_approved_temp = 'REJECTED' WHERE p.id = a.id;
UPDATE project AS p, (select id from project where other_documents_approved = 1) AS a
SET p.other_documents_approved_temp = 'APPROVED' WHERE p.id = a.id;

ALTER TABLE project DROP COLUMN other_documents_approved,
CHANGE COLUMN other_documents_approved_temp other_documents_approved ENUM('UNSET', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'UNSET';