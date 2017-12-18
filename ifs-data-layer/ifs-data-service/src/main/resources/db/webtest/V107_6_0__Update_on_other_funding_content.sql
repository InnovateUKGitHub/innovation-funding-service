--Update the first line of the content on Other funding question
update question
set description='Tell us if you have received any other public sector funding for this project. This information is important as other public sector support counts as part of the funding you can receive for your project.'
where description like '%or received any other public sector funding for this project. This information is important as other public sector%' and question_type <> 'GENERAL';