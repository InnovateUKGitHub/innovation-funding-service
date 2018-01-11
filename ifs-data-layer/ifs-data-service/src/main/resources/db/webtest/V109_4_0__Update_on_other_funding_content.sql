-- Script to update the first line of the content on Other funding question.
-- Have been discussed to place this to webtest folder and to be applied after the webtest data (IFS-2317)
UPDATE question
SET description='Tell us if you have received any other public sector funding for this project. This information is important as other public sector support counts as part of the funding you can receive for your project.'
WHERE description LIKE '%or received any other public sector funding for this project. This information is important as other public sector%' AND question_type <> 'GENERAL';