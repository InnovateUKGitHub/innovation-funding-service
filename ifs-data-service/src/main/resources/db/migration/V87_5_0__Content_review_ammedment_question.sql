update question set
description='Please provide a short summary of your project. We will not score this summary.',
short_name='Project summary'
where short_name='Project summary\n';

update question
set description='If your application doesn\'t align with the scope, we will not assess it.'
where short_name='Scope';