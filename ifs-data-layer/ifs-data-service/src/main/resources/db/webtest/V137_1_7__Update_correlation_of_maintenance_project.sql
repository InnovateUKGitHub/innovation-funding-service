-- update project details
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('15', '52', '110', 'PROJECT_MANAGER', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('15', '52', '110', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('15', '36', '67', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('15', '43', '78', 'PROJECT_FINANCE_CONTACT', '1');

UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='31';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='32';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='33';

UPDATE process SET event='project-manager-added', last_modified=now(), activity_state_id='8' WHERE id='386';

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
UPDATE project SET address=LAST_INSERT_ID() WHERE id='15';