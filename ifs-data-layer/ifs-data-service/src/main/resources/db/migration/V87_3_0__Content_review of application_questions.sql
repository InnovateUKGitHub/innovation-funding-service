update question
set description='Please provide a short summary of your project. We will not score this summary.'
where short_name='Project summary';

update question
set description='Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but is not scored.'
where short_name='Public description';

update question
set description='How does your project align with the scope of this competition? If your application doesn\'t align with the scope, we will not assess it.'
where short_name='Scope';