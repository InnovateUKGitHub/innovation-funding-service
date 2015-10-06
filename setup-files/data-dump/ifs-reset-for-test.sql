TRUNCATE question_status; 										# Remove all question statuses so no question is already assigned or marked as complete
UPDATE response SET value=''; 									# Reset all responses to nothing so all questions start empty
UPDATE cost SET cost=0, quantity=0,item=''; 					# reset all costs to zero for testing the application form
UPDATE process SET status='pending' WHERE id='3' OR id='4'; 	# Set two applications up as pending for Assessor testing
UPDATE process SET status='assessed' WHERE id='1'; 	# Set two applications up as pending for Assessor testing
UPDATE assessment SET submitted='Y' WHERE application=1; 		# Set first application to submitted for Assessor testing
UPDATE assessment SET submitted='Y' WHERE process_id=2;			# Set second application to submitted for Assessor testing
UPDATE assessment SET submitted='N' WHERE application=3; 		# Set first application to submitted for Assessor testing
UPDATE response SET assessment_feedback=NULL, assessment_score=NULL WHERE question_id='13' and application_id='5'; # Reset the first question feedback response for Assessor testing.
UPDATE assessment SET temp_recommended_value='EMPTY', recommendation_feedback='test' WHERE application=6 # Reset the recommendation feedback for application 6 for Assessor testing.