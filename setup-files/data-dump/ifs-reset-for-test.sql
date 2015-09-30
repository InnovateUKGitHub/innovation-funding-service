TRUNCATE question_status;
UPDATE response SET value='';
UPDATE cost SET cost=0, quantity=0,item='';
UPDATE process SET status='PENDING' WHERE id='3' OR id='4';
UPDATE assessment SET temp_recommended_value='EMPTY', recommendation_feedback='test' WHERE application=6