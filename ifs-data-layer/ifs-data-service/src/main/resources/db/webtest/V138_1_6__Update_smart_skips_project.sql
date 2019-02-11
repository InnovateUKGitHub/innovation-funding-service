-- update project details
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('16', '115', '169', 'PROJECT_MANAGER', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('16', '115', '169', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('16', '21', '68', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('16', '37', '69', 'PROJECT_FINANCE_CONTACT', '1');

UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='34';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='35';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='36';

UPDATE process SET event='project-manager-added', last_modified=now(), activity_state_id='8' WHERE id='396';

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
UPDATE project SET address=LAST_INSERT_ID() WHERE id='16';