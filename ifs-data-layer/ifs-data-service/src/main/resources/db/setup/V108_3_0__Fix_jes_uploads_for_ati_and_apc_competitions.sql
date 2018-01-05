-- IFS-2396: Update Je-s uploads for new competitions

-- Add Je-s upload question back to APC competition
SET @apc_template_id = (SELECT id FROM competition WHERE name = "Template for the Advanced Propulsion Centre competition type");
SET @your_funding_section_id = (SELECT id FROM section WHERE competition_id = @apc_template_id AND name = "Your funding");
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,NULL,1,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'','Je-s Output',20,NULL,@apc_template_id,@your_funding_section_id,NULL,'GENERAL');
SET @q_jes_output=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,20,@apc_template_id,1,'Upload a pdf copy of the Je-S output form once you have a status of \'With Council\'.','How do I create my Je-S output?','<p>You should include only supporting information in the appendix. You shouldn’t use it to provide your responses to the question.</p><p>Guidance for this section needs to be created</p>',0,@q_jes_output,'APPLICATION',1);

-- Update Je-s section information
UPDATE question q
INNER JOIN section s
ON q.competition_id=s.competition_id
SET q.section_id=s.id
WHERE q.short_name='Je-s Output'
AND s.name='Your project costs';

-- Update J-es form input guidance
SET @finance_upload_id = (SELECT id FROM form_input_type WHERE name = "FINANCE_UPLOAD");
UPDATE `form_input` f
INNER JOIN question q
ON q.id = f.question_id
SET
f.guidance_title=NULL,
f.guidance_answer=NULL,
q.description='<a target="_blank" href="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system">How do I create my Je-S output?</a>'
WHERE f.form_input_type_id=@finance_upload_id;



