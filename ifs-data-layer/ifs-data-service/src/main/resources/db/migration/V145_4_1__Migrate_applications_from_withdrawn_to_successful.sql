-- IFS-5966

SET @application_withdrawn_activity_state_id =
(SELECT id from activity_state
WHERE activity_type = 'APPLICATION'
AND state = 'WITHDRAWN');

SET @application_successful_activity_state_id =
(SELECT id from activity_state
WHERE activity_type = 'APPLICATION'
AND state = 'ACCEPTED');

-- update application event to be successful
UPDATE process SET event = 'approved'
WHERE activity_state_id = @application_withdrawn_activity_state_id;

-- update application state to be successful
UPDATE process SET activity_state_id = @application_successful_activity_state_id
WHERE activity_state_id = @application_withdrawn_activity_state_id;

-- remove application withdrawn state entirely
DELETE FROM activity_state where id = @application_withdrawn_activity_state_id;