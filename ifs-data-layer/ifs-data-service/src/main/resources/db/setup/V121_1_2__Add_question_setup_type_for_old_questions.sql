-- IFS-2833 Set question_setup_type for old competitions
UPDATE question
SET question_setup_type = 'APPLICATION_DETAILS'
WHERE short_name = 'Application details';

UPDATE question
SET question_setup_type = 'SCOPE'
WHERE short_name = 'Scope';

UPDATE question
SET question_setup_type = 'PROJECT_SUMMARY'
WHERE short_name = 'Project summary';

UPDATE question
SET question_setup_type = 'PUBLIC_DESCRIPTION'
WHERE short_name = 'Public description';