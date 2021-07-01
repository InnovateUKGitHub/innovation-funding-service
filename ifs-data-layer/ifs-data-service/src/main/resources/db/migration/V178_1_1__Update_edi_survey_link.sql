update question
 set description = REPLACE(description, 'https://www.surveymonkey.co.uk/r/ifsaccount', 'https://bit.ly/EDIForm')
 where short_name = 'Equality, diversity and inclusion'
 and competition_id in (
  select id from competition where funding_type = 'LOAN'
 );
