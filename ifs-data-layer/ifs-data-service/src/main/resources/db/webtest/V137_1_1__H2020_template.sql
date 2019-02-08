INSERT INTO competition (name, max_research_ratio, academic_grant_percentage, multi_stream, assessor_count, assessor_pay, has_assessment_panel, has_interview_stage, assessor_finance_view, template, use_resubmission_question, min_project_duration, max_project_duration, non_ifs, terms_and_conditions_id, location_per_partner, state_aid, include_project_growth_table, include_your_organisation_section, created_by, created_on, modified_on, modified_by)
VALUES ('Template for the Horizon 2020 competition type', '0', '100', '0', '0', '0', 0, 0, 'OVERVIEW', 1, 1, '1', '36', 0, '6', 0, 1, '0', '0', '15', '1970-01-01 00:00:01', '1970-01-01 00:00:01', '15');
set @template_id = (SELECT LAST_INSERT_ID());

-- Sections
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,'Please provide Innovate UK with information about your project.',1,'Project details',1,@template_id,NULL,0,'GENERAL');
set @section_project_details_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Application questions',2,@template_id,NULL,0,'GENERAL');
set @section_application_questions_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Finances',3,@template_id,NULL,0,'GENERAL');
set @section_finances_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Your finances',4,@template_id,@section_finances_id,1,'FINANCE');
set @section_your_finances_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Your project costs',5,@template_id,@section_your_finances_id,1,'PROJECT_COST_FINANCES');
set @section_your_project_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Your project location',6,@template_id,@section_your_finances_id,1,'PROJECT_LOCATION');
set @section_project_location_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Your organisation',7,@template_id,@section_your_finances_id,1,'ORGANISATION_FINANCES');
set @section_your_organisation_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Your funding',8,@template_id,@section_your_finances_id,1,'FUNDING_FINANCES');
set @section_your_funding_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Labour',1,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_labour_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Overhead costs',2,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_overhead_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Materials',3,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_material_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Capital usage',4,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_capital_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Subcontracting costs',5,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_subcontracting_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Travel and subsistence',6,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_travel_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Other costs',7,@template_id,@section_your_project_costs_id,0,'GENERAL');
set @section_other_costs_id = (SELECT LAST_INSERT_ID());
INSERT INTO section (assessor_guidance_description,description,display_in_assessment_application_summary,name,priority,competition_id,parent_section_id,question_group,section_type) VALUES (NULL,NULL,1,'Finances overview',9,@template_id,@section_finances_id,1,'OVERVIEW_FINANCES');
set @section_finances_overview_id = (SELECT LAST_INSERT_ID());

-- Questions
-- Project details
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Enter the full title of the project',1,0,'Application details','Application details',2,NULL,@template_id,@section_project_details_id,NULL,'LEAD_ONLY','APPLICATION_DETAILS');
set @question_application_details_id = (SELECT LAST_INSERT_ID());
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Description not used',1,0,'Application team','Application team',1,NULL,@template_id,@section_project_details_id,NULL,'LEAD_ONLY','APPLICATION_TEAM');
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Description not used',1,0,'Research category','Research category',3,NULL,@template_id,@section_project_details_id,NULL,'LEAD_ONLY','RESEARCH_CATEGORY');
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please provide a short summary of your project. Make sure you include what is innovative about it.',1,0,'Project summary','Project summary',4,NULL,@template_id,@section_project_details_id,NULL,'GENERAL','PROJECT_SUMMARY');
set @question_project_summary_id = (SELECT LAST_INSERT_ID());
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but we will not assess this content as part of your application.',1,0,'Public description','Public description',5,NULL,@template_id,@section_project_details_id,NULL,'GENERAL','PUBLIC_DESCRIPTION');
set @question_public_description_id = (SELECT LAST_INSERT_ID());

-- Application questions
-- TODO What happens with the H2020 question?
--INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Horizon 2020',1,0,'Horizon 202','Horizon 2020',5,NULL,@template_id,@section_application_questions_id,NULL,'GENERAL',NULL);

-- Project costs
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,NULL,1,1,'Provide the project costs for \'{organisationName}\'','Project finances',17,NULL,@template_id,@section_your_project_costs_id,NULL,'GENERAL',NULL);
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'<a target=\"_blank\" href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system\">How do I create my Je-S output?</a>',1,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'','Je-s Output',22,NULL,@template_id,@section_your_project_costs_id,NULL,'GENERAL',NULL);

-- Project location
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Description not used',1,1,'Project location question','Project location question',2,NULL,@template_id,@section_project_location_id,NULL,'GENERAL',NULL);

-- Your organisation
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'To determine the level of funding you are eligible to receive please provide your business size using the <a href=\"http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm\" target=\"_blank\" rel=\"external\">EU Definition</a> for guidance.',1,1,'Size','Business size',20,NULL,@template_id,@section_your_organisation_id,NULL,'GENERAL',NULL);
set @question_organisation_size_id = (SELECT LAST_INSERT_ID());

-- Your funding
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Please tell us if you have ever applied for or received any other public sector funding for this project. You should also include details of any offers of funding you\'ve received.',1,1,'Other funding','Other funding',22,NULL,@template_id,@section_your_funding_id,NULL,'COST',NULL);
set @question_other_funding_id = (SELECT LAST_INSERT_ID());
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'Please enter the funding level that you would like to apply for in this application',1,1,'Funding level','Funding level',21,NULL,@template_id,@section_your_funding_id,NULL,'GENERAL',NULL);
set @question_funding_level_id = (SELECT LAST_INSERT_ID());

-- Labour
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,NULL,1,1,'Labour',NULL,2,NULL,@template_id,@section_labour_costs_id,NULL,'COST',NULL);
set @question_labour_id = (SELECT LAST_INSERT_ID());

-- Overheads
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'<p>You may incur indirect support staff costs linked with your administrative work for the project. To be eligible, these costs should be directly attributable and incremental to the project. Indirect costs associated with commercial activities are not eligible and must not be included. For further information on which costs are eligible please read our <a href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance\">project costs guidance</a>.</p>',1,1,'Overheads','Indirect costs',4,NULL,@template_id,@section_overhead_costs_id,NULL,'GENERAL',NULL);
set @question_overhead_id = (SELECT LAST_INSERT_ID());

-- Material
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please provide a breakdown of the materials you expect to use during the project',1,1,'Materials','Materials',2,NULL,@template_id,@section_material_costs_id,NULL,'COST',NULL);
set @question_material_id = (SELECT LAST_INSERT_ID());

-- Capital usage
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please provide a breakdown of the capital items you will buy and/or use for the project.',1,1,'Capital Usage','Capital items',2,NULL,@template_id,@section_capital_costs_id,NULL,'COST',NULL);
set @question_capital_id = (SELECT LAST_INSERT_ID());

-- Subcontracting
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please provide details of any work that you expect to subcontract for your project.',1,1,'Sub-contracting costs','Sub-contracts',2,NULL,@template_id,@section_subcontracting_costs_id,NULL,'COST',NULL);
set @question_subcontracting_id = (SELECT LAST_INSERT_ID());

-- Travel
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,NULL,1,1,'Travel and subsistence',NULL,2,NULL,@template_id,@section_travel_costs_id,NULL,'COST',NULL);
set @question_travel_id = (SELECT LAST_INSERT_ID());

-- Other costs
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,'Please note that legal or project audit and accountancy fees are not eligible and should not be included as an \'other cost’. Patent filing costs of New IP relating to the project are limited to £7,500 for SME applicants only.  Please provide estimates of other costs that do not fit within any other cost headings.',1,1,'Other costs','Other costs',2,NULL,@template_id,@section_other_costs_id,NULL,'COST',NULL);
set @question_other_id = (SELECT LAST_INSERT_ID());

-- Finance overview
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (1,NULL,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,19,NULL,@template_id,@section_finances_overview_id,NULL,'GENERAL',NULL);
set @question_finance_summary_id = (SELECT LAST_INSERT_ID());
INSERT INTO question (assign_enabled,description,mark_as_completed_enabled,multiple_statuses,name,short_name,priority,question_number,competition_id,section_id,assessor_maximum_score,question_type,question_setup_type) VALUES (0,'',0,0,NULL,NULL,18,NULL,@template_id,@section_finances_overview_id,NULL,'GENERAL',NULL);

-- Form Inputs
-- project details
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,5,4,1,'Application details',NULL,NULL,0,@question_application_details_id ,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (400,2,4,1,'Project summary','What should I include in the project summary?','<p>We will not score this summary, but it will give the assessors a useful introduction to your project. It should provide a clear overview of the whole project, including:</p> <ul class=\"list-bullet\">         <li>your vision for the project</li><li>key objectives</li><li>main areas of focus</li><li>details of how it is innovative</li></ul>',0,@question_project_summary_id,'APPLICATION',1,NULL);
set @fi_project_summary_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (400,2,4,1,'Public description','What should I include in the project public description?','<p>Innovate UK publishes information about projects we have funded. This is in line with government practice on openness and transparency of public-funded activities.</p><p>Describe your project in a way that will be easy for a non-specialist to understand. Don\'t include any information that is confidential, for example, intellectual property or patent details.</p> ',0,@question_public_description_id,'APPLICATION',1,NULL);
set @fi_public_description_id = (SELECT LAST_INSERT_ID());

-- your organisation
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,19,4,1,'Size',NULL,NULL,0,@question_organisation_size_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,24,4,0,'Turnover (£)','Your turnover from the last financial year.','',2,@question_organisation_size_id,'APPLICATION',1,NULL);
set @fi_turnover_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,25,4,0,'Full time employees','Number of full time employees at your organisation.','',3,@question_organisation_size_id,'APPLICATION',1,NULL);
set @fi_employees_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,26,4,0,'End of last financial year','Enter the date of your last financial year.','',4,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_end_fin_year_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,27,4,0,'Annual turnover','','',5,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_fin_turnover_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,27,4,0,'Annual profits','','',6,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_fin_profit_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,27,4,0,'Annual export','','',7,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_fin_export_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,27,4,0,'Research and development spend','','',8,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_fin_rd_id = (SELECT LAST_INSERT_ID());
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,28,4,0,'Full time employees','How many full-time employees did you have on the project at the close of your last financial year?','',9,@question_organisation_size_id,'APPLICATION',0,NULL);
set @fi_fin_employees_id = (SELECT LAST_INSERT_ID());

-- your funding
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,17,4,1,'Other funding','What should I include in the other public funding section?','<p>You must provide details of other public funding that you are currently applying for (or have already applied for) in relation to this project. You do not need to include completed grants that were used to reach this point in the development process. This information is important as other public sector support counts as part of the funding you can receive for your project.</p>',0,@question_other_funding_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,7,4,1,'Please enter the grant % you wish to claim for this project','What funding level should I enter?','<p>For a business or academic organisation, you can apply for any funding percentage between 0% and the maximum allowable for your organisation size. For other organisation types, you can apply for any funding percentage between 0% and 100%. The amount you apply for must reflect other funding you may have received. It must also be within participation levels which you can review on the Finances Overview page.</p>',0,@question_funding_level_id,'APPLICATION',1,NULL);

INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,8,4,1,'Labour',NULL,NULL,0,@question_labour_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,9,4,1,'Overheads',NULL,NULL,0,@question_overhead_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,10,4,1,'Materials',NULL,NULL,0,@question_material_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,11,4,1,'Capital Usage',NULL,NULL,0,@question_capital_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,12,4,1,'Sub-contracting costs',NULL,NULL,0,@question_subcontracting_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,13,4,1,'Travel and subsistence',NULL,NULL,0,@question_travel_id,'APPLICATION',1,NULL);
INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,14,4,1,'Other costs',NULL,NULL,0,@question_other_id,'APPLICATION',1,NULL);

INSERT INTO form_input (word_count,form_input_type_id,competition_id,included_in_application_summary,description,guidance_title,guidance_answer,priority,question_id,scope,active,allowed_file_types) VALUES (NULL,16,4,1,NULL,NULL,NULL,0,@question_finance_summary_id,'APPLICATION',1,NULL);

INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_project_summary_id,2);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_project_summary_id,3);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_public_description_id,2);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_public_description_id,3);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_turnover_id,4);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_employees_id,4);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_end_fin_year_id,6);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_fin_turnover_id,5);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_fin_profit_id,5);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_fin_export_id,5);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_fin_rd_id,5);
INSERT INTO form_input_validator (form_input_id,form_validator_id) VALUES (@fi_fin_employees_id,4);


INSERT INTO competition_type (name, active, template_competition_id) VALUES ('Horizon 2020', 1, @template_id);


