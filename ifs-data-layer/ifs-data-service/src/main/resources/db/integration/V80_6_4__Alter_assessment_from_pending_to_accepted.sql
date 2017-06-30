SELECT @acceptedactivitystateid := id from activity_state where activity_type = 'APPLICATION_ASSESSMENT' AND state = 'ACCEPTED';
SELECT @felixwilsonid := id FROM user where email = 'felix.wilson@gmail.com';
SELECT @securityiotapplicationid := id from application where name = 'Security for the Internet of Things';
SELECT @felixwilsonsecurityiotparticipantid := id from process_role where application_id = @securityiotapplicationid and user_id = @felixwilsonid;
UPDATE process set activity_state_id = @acceptedactivitystateid where participant_id = @felixwilsonsecurityiotparticipantid;