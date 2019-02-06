-- update project details
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('6', '32', '60', 'PROJECT_MANAGER', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('6', '32', '60', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('6', '36', '67', 'PROJECT_FINANCE_CONTACT', '1');
INSERT INTO project_user (project_id, organisation_id, user_id, project_role, participant_status_id) VALUES ('6', '33', '62', 'PROJECT_FINANCE_CONTACT', '1');

UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='10';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='9';
UPDATE partner_organisation SET postcode='RG8 7TE' WHERE id='8';

UPDATE process SET event='project-manager-added', last_modified=now(), activity_state_id='8' WHERE id='304';

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
UPDATE project SET address=LAST_INSERT_ID() WHERE id='6';

-- add project documents
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'CollaborationAgreement.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status) VALUES ('6', '65', LAST_INSERT_ID(), 'APPROVED');
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'ExploitationPlan.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status)  VALUES ('6', '72', LAST_INSERT_ID(), 'APPROVED');

-- add monitoring officer
INSERT INTO monitoring_officer (first_name, last_name, email, phone_number, project_id) VALUES ('Paul', 'Hollywood', 'p.hollywood@bake.example.com', '01675 342514', '6');

-- bank details
INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '32', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '6', LAST_INSERT_ID(), '32', '10', 0, '10', 1, 1);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '36', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '6', LAST_INSERT_ID(), '36', '10', 0, '10', 1, 1);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county) VALUES ('Bag End', 'Bagshot Row', '', 'Hobbiton', 'E17 5LR', 'The Shire');
INSERT INTO organisation_address (address_id, organisation_id, address_type_id) VALUES (LAST_INSERT_ID(), '33', '4');
INSERT INTO bank_details (sort_code, account_number, project_id, organisation_address_id, organisation_id, company_name_score, registration_number_matched, address_score, manual_approval, verified) VALUES ('404745', '51406795', '6', LAST_INSERT_ID(), '33', '10', 0, '10', 1, 1);

-- finance checks
UPDATE project_finance SET viability_status='GREEN', credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='8';
UPDATE project_finance SET credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='9';
UPDATE project_finance SET viability_status='GREEN', credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='10';

UPDATE process SET last_modified=now(), activity_state_id='23' WHERE id='305';
UPDATE process SET last_modified=now(), activity_state_id='23' WHERE id='307';
UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='308';
UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='309';
UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='310';

-- spend profile
INSERT INTO cost_category_group (description) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs');
INSERT INTO cost_category_group (description) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs');

INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');

INSERT INTO cost_category_type (name, cost_category_group_id) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs', '11');
INSERT INTO cost_category_type (name, cost_category_group_id) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs', '12');

INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES ('12', '46', '32', '6', '47', 1, now(), '17');
INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES ('13', '48', '33', '6', '49', 1, now(), '17');
INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES( '4', '50', '36', '6', '51', 01, now(), '17');

UPDATE project SET spend_profile_submitted_date=now() WHERE id='6';

UPDATE process SET activity_state_id='41' WHERE id='313';


