SET SQL_SAFE_UPDATES = 0; # Ignore warnings when using a update without PK in the WHERE clause

TRUNCATE question_status; 										# Remove all question statuses so no question is already assigned or marked as complete
UPDATE response SET value=''; 									# Reset all responses to nothing so all questions start empty
UPDATE cost SET cost=0, quantity=0,item=''; 					# reset all costs to zero for testing the application form
UPDATE process SET status='pending' WHERE id='3' OR id='4'; 	# Set two applications up as pending for Assessor testing
UPDATE process SET status='assessed' WHERE id='1'; 	# Set two applications up as pending for Assessor testing
UPDATE assessment SET submitted='Y' WHERE application=1; 		# Set first application to submitted for Assessor testing
UPDATE assessment SET submitted='Y' WHERE process_id=2;			# Set second application to submitted for Assessor testing
UPDATE assessment SET submitted='N' WHERE application=3; 		# Set first application to submitted for Assessor testing
UPDATE response SET assessment_feedback=NULL, assessment_score=NULL WHERE question_id='13' and application_id='5'; # Reset the first question feedback response for Assessor testing.
UPDATE response SET `update_date`='2015-09-18 10:33:27', `value`='Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly. \r \r We propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r' WHERE `id`='1'; # Set the last updated date for question 1
UPDATE assessment SET temp_recommended_value='EMPTY', recommendation_feedback='test' WHERE application=6; # Reset the recommendation feedback for application 6 for Assessor testing.
UPDATE application SET `application_status_id`='1' WHERE `id`='1'; # reset the application to the unsubmitted status before test.




SET SQL_SAFE_UPDATES = 1;