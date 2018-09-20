-- IFS-3980
-- Set all of the webtest users to have already agreed to the new terms and conditions
-- Needed since otherwise changing the terms and conditions will break all of the acceptance tests
-- In the long term will be replaced with proper webtest data

SET @new_terms_and_conditions_id =
(SELECT id FROM terms_and_conditions WHERE type = 'SITE' AND version = 2);

-- Set the new terms and conditions as agreed for all users
UPDATE user_terms_and_conditions SET terms_and_conditions_id = @new_terms_and_conditions_id;