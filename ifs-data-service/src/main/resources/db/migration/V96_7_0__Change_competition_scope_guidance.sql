-- Update all scope guidance (including template competitions)

UPDATE form_input SET
  guidance_answer='You should still assess this application even if you think that it is not in scope. Your answer should be based upon the following:'
WHERE
  description='Feedback' AND
  guidance_title='Guidance for assessing scope' AND
  scope='ASSESSMENT';

UPDATE guidance_row SET
  subject='No',
  priority=1
WHERE
  subject='NO' AND
  form_input_id IN (SELECT id FROM form_input WHERE
  description='Feedback' AND
  guidance_title='Guidance for assessing scope' AND
  scope='ASSESSMENT');

UPDATE guidance_row SET
  subject='Yes',
  priority=0
WHERE
  subject='YES' AND
  form_input_id IN (SELECT id FROM form_input WHERE
  description='Feedback' AND
  guidance_title='Guidance for assessing scope' AND
  scope='ASSESSMENT');
