ALTER TABLE project_document MODIFY status enum('UPLOADED','SUBMITTED','APPROVED','REJECTED','REJECTED_DUE_TO_TEAM_CHANGE') NOT NULL;