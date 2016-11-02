-- Change the status of Juggling Craziness Applications so that they are Submitted rather than Approved and Rejected
SELECT @jugglingcrazinesscompetitionid := id FROM competition where name = 'Juggling Craziness';
SELECT @submittedstatusid := id FROM application_status where name = 'submitted';
SELECT @approvedstatusid := id FROM application_status where name = 'approved';
SELECT @rejectedstatusid := id FROM application_status where name = 'rejected';

UPDATE application SET application_status_id = @submittedstatusid WHERE competition = @jugglingcrazinesscompetitionid AND application_status_id IN (@approvedstatusid, @rejectedstatusid);

-- Assign Assessors on Applications 'Juggling is very fun' and 'Juggling is not fun'
SELECT @assessorroleid := id from role where name = 'assessor';
SELECT @paulplumid := id FROM user where email = 'paul.plum@gmail.com';
SELECT @felixwilsonid := id FROM user where email = 'felix.wilson@gmail.com';
SELECT @injugglingwetrustid := id from organisation where name = 'In Juggling We Trust Ltd';
SELECT @jugglingbuffooneryid := id from organisation where name = 'Juggling Buffoonery Ltd';
SELECT @jugglingisveryfunapplicationid := id from application where name = 'Juggling is very fun';
SELECT @jugglingisnotfunapplicationid := id from application where name = 'Juggling is not fun';

INSERT INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@jugglingisveryfunapplicationid,@injugglingwetrustid,@assessorroleid,@paulplumid);
SELECT @paulplumjugglingisveryfunparticipantid := LAST_INSERT_ID();

INSERT INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@jugglingisnotfunapplicationid,@injugglingwetrustid,@assessorroleid,@paulplumid);
SELECT @paulplumjugglingisnotfunparticipantid := LAST_INSERT_ID();

INSERT INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@jugglingisveryfunapplicationid,@jugglingbuffooneryid,@assessorroleid,@felixwilsonid);
SELECT @felixwilsonjugglingisveryfunparticipantid := LAST_INSERT_ID();

INSERT INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@jugglingisnotfunapplicationid,@jugglingbuffooneryid,@assessorroleid,@felixwilsonid);
SELECT @felixwilsonjugglingisnotfunparticipantid := LAST_INSERT_ID();

-- Create Accepted Assessments for Applications 'Juggling is very fun' and 'Juggling is not fun'
SELECT @acceptedactivitystateid := id from activity_state where activity_type = 'APPLICATION_ASSESSMENT' AND state = 'ACCEPTED';
INSERT INTO `process` (`event`, `last_modified`, `process_type`, `target_id`, `participant_id`, `activity_state_id`) VALUES ('accept','2016-07-11 12:08:36','Assessment',@jugglingisveryfunapplicationid,@paulplumjugglingisveryfunparticipantid,@acceptedactivitystateid);
INSERT INTO `process` (`event`, `last_modified`, `process_type`, `target_id`, `participant_id`, `activity_state_id`) VALUES ('accept','2016-07-12 17:13:04','Assessment',@jugglingisnotfunapplicationid,@paulplumjugglingisnotfunparticipantid,@acceptedactivitystateid);
INSERT INTO `process` (`event`, `last_modified`, `process_type`, `target_id`, `participant_id`, `activity_state_id`) VALUES ('accept','2016-07-14 10:43:11','Assessment',@jugglingisveryfunapplicationid,@felixwilsonjugglingisveryfunparticipantid,@acceptedactivitystateid);
INSERT INTO `process` (`event`, `last_modified`, `process_type`, `target_id`, `participant_id`, `activity_state_id`) VALUES ('accept','2016-07-11 12:10:44','Assessment',@jugglingisnotfunapplicationid,@felixwilsonjugglingisnotfunparticipantid,@acceptedactivitystateid);