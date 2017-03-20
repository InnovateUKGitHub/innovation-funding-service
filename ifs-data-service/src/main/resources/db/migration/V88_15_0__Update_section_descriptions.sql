UPDATE section SET description = 'These are the questions which will be marked by the assessors.'
WHERE name='Application questions'
AND description='These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.';

UPDATE section SET description = 'Please provide information about your project. This section is not scored but will provide background to the project.'
WHERE name='Project details'
AND description='Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.';

UPDATE section SET description = 'Each organisation is required to submit their own project finances. The project costs for all participants can be seen in the \'Finances overview\'.'
WHERE name='Finances'
AND description='Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the \'Finances overview\' section';
