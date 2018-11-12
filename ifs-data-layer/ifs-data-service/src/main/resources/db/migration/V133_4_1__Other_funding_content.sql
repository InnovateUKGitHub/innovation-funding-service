-- IFS-2610 Updates question text for funding source

UPDATE question
SET description='Have you received any other public sector funding for this project? This is important as other public sector support counts as part of the funding you can receive.'
WHERE
  question_type <> 'GENERAL' AND
  description LIKE 'Tell us if you have received any other public sector funding for this project. This information is important as other public sector support counts as part of the funding you can receive for your project.';