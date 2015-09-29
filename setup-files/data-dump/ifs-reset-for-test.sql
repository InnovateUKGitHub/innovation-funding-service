TRUNCATE question_status;
UPDATE response SET value='';
UPDATE cost SET cost='', quantity='',item='';
UPDATE process SET status='PENDING' WHERE id='3' OR id='4';