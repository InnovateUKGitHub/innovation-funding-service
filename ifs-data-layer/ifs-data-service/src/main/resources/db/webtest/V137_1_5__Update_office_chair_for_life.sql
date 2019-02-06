-- update project details
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('5', '31', '58', 'PROJECT_MANAGER', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('5', '31', '58', 'PROJECT_FINANCE_CONTACT', '1');

UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='7';

UPDATE process SET event='project-manager-added', last_modified=now(), activity_state_id='8' WHERE id='298';

-- add project documents
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'ExploitationPlan.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status)  VALUES ('5', '72', LAST_INSERT_ID(), 'APPROVED');

-- add monitoring officer
INSERT INTO monitoring_officer (first_name, last_name, email, phone_number, project_id) VALUES ('Paul', 'Hollywood', 'p.hollywood@bake.example.com', '01675 342514', '5');
