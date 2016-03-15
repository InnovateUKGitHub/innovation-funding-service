SET SQL_SAFE_UPDATES = 0; # Ignore warnings when using a update without PK in the WHERE clause

TRUNCATE question_status; 										# Remove all question statuses so no question is already assigned or marked as complete
UPDATE cost SET cost=0, quantity=0,item=''; 					# reset all costs to zero for testing the application form
UPDATE application SET application_status_id='1' WHERE id=1; #Unsubmit first application for Lead Applicant form testing
UPDATE process SET status='pending' WHERE id=3 OR id=4; 	# Set two applications up as pending for Assessor testing
UPDATE process SET status='assessed' WHERE id=1; 	# Set two applications up as pending for Assessor testing
UPDATE assessment SET submitted='Y' WHERE process_id=2;			# Set second application to submitted for Assessor testing
UPDATE assessment SET submitted='N' WHERE application=3; 		# Set first application to submitted for Assessor testing
#UPDATE response SET assessment_feedback=NULL, assessment_score=NULL WHERE question_id='13' and application_id='5'; # Reset the first question feedback response for Assessor testing.
UPDATE response SET `update_date`='2015-09-18 10:33:27' WHERE `id`='1'; # Set the last updated date for question 1
UPDATE assessment SET temp_recommended_value='EMPTY', recommendation_feedback='test' WHERE application=6; # Reset the recommendation feedback for application 6 for Assessor testing.
UPDATE application SET `application_status_id`='1' WHERE `id`='1'; # reset the application to the unsubmitted status before test.




SET SQL_SAFE_UPDATES = 1;