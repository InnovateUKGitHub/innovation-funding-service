-- IFS-5138: Update New materials for lighter stock project.

-- update project details
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('9', '41', '75', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('9', '41', '75', 'PROJECT_MANAGER', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('9', '42', '118', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('9', '43', '78', 'PROJECT_FINANCE_CONTACT', '1');

UPDATE process SET event='project-manager-added', last_modified=now(), activity_state_id='8' WHERE id='326';

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
UPDATE project SET address=LAST_INSERT_ID() WHERE `id`='9';

-- add monitoring officer
INSERT INTO monitoring_officer (first_name, last_name, email, phone_number, project_id) VALUES ('Paul', 'Hollywood', 'p.hollywood@bake.example.com', '01675 342514', '9');

-- add project documents
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'CollaborationAgreement.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status) VALUES ('9', '66', LAST_INSERT_ID(), 'APPROVED');
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'ExploitationPlan.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status)  VALUES ('9', '73', LAST_INSERT_ID(), 'APPROVED');

-- bank details
INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '41', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '9', LAST_INSERT_ID(), '41', '10', 0, '10', 0, 1);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '42', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '9', LAST_INSERT_ID(), '42', '10', 0, '10', 1, 1);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '43', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '9', LAST_INSERT_ID(), '43', '10', 0, '10', 1, 1);
