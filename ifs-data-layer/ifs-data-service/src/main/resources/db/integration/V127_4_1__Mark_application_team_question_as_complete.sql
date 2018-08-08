SET @application_team_question_id = (SELECT q.id from question q WHERE q.competition_id = 1 AND q.question_setup_type =
'APPLICATION_TEAM');

SET @lead_applicant_role_id = (SELECT id from role where name = 'leadapplicant');

SET @lead_applicant_id_application_1 = (SELECT id from process_role where application_id = 1 AND role_id =
@lead_applicant_role_id);

SET @lead_applicant_id_application_7 = (SELECT id from process_role where application_id = 7 AND role_id =
@lead_applicant_role_id);

INSERT INTO question_status (application_id, question_id, marked_as_complete_by_id, marked_as_complete)
VALUES (1, @application_team_question_id, @lead_applicant_id_application_1, true);

INSERT INTO question_status (application_id, question_id, marked_as_complete_by_id, marked_as_complete)
VALUES (7, @application_team_question_id, @lead_applicant_id_application_7, true);