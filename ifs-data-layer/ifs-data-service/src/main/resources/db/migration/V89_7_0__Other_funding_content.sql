update question
set description='Please tell us if you have applied for, or received any other public sector funding for this project. This information is important as other public sector support counts as part of the funding you can receive for your project.'
where name='Other funding';

update form_input f
inner join question q on f.question_id=q.id
set
guidance_title='What should I include in other funding?',
guidance_answer='<p>You do not need to include completed grants that were used to reach this point in the development process.</p>'
where q.name='Other funding';


