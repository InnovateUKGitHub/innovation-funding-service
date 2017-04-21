UPDATE `form_input` f
INNER JOIN question q
ON q.id = f.question_id
SET
f.guidance_title=NULL,
f.guidance_answer=NULL,
q.description='<a target="_blank" href="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system">How do I create my Je-S output?</a>'
WHERE f.form_input_type_id=20;