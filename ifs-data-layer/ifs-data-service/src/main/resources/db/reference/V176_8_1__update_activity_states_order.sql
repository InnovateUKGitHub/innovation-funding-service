-- Update so that the activity states are in a correct order

UPDATE activity_state SET state = 'REJECTED' WHERE id = 58;
UPDATE activity_state SET state = 'ACCEPTED' WHERE id = 57;