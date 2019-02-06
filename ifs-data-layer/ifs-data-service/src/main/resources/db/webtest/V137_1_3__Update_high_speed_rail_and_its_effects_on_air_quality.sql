-- add project documents
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'CollaborationAgreement.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status) VALUES ('12', '66', LAST_INSERT_ID(), 'APPROVED');
INSERT INTO file_entry (filesize_bytes, media_type, name) VALUES ('7945', 'application/pdf', 'ExploitationPlan.pdf');
INSERT INTO project_document (project_id, document_config_id, file_entry_id, status)  VALUES ('12', '73', LAST_INSERT_ID(), 'APPROVED');

-- approve bank details
UPDATE bank_details SET manual_approval=1 WHERE id='3';
UPDATE bank_details SET manual_approval=1 WHERE id='4';
UPDATE bank_details SET manual_approval=1 WHERE id='5';

-- finance checks
UPDATE project_finance SET viability_status='GREEN', credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='22';
UPDATE project_finance SET viability_status='GREEN', credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='23';
UPDATE project_finance SET viability_status='GREEN', credit_report_confirmed=1, eligibility_status='GREEN' WHERE id='24';

UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='360';
UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='361';
UPDATE process SET last_modified=now(), activity_state_id='26' WHERE id='362';
UPDATE process SET last_modified=now(), activity_state_id='23' WHERE id='357';
UPDATE process SET last_modified=now(), activity_state_id='23' WHERE id='358';

-- spend profile
INSERT INTO cost_category_group (description) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs');
INSERT INTO cost_category_group (description) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs');

INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Eligible costs for Partner Organisation');
INSERT INTO cost_group (description) VALUES ('Spend Profile figures for Partner Organisation');

INSERT INTO cost_category_type (name, cost_category_group_id) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs', '9');
INSERT INTO cost_category_type (name, cost_category_group_id) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs', '10');
INSERT INTO cost_category_type (name, cost_category_group_id) VALUES ('Cost Category Type for Categories Labour, Overheads, Materials, Capital usage, Subcontracting, Travel and subsistence, Other costs', '10');

INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES ('9', '40', '50', '12', '41', 1, now(), '17');
INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES ('10', '42', '51', '12', '43', 1, now(), '17');
INSERT INTO spend_profile (cost_category_type_id, eligible_costs_cost_group_id, organisation_id, project_id, spend_profile_figures_cost_group_id, marked_as_complete, generated_date, generated_by_id) VALUES( '11', '44', '52', '12', '45', 1, now(), '17');

UPDATE project SET spend_profile_submitted_date=now() WHERE id='12';

UPDATE process SET activity_state_id='42' WHERE id='365';

