-- IFS-3088 - Temporarily modify test data to allow testing for backwards compatability for this feature
--   Remove the new application team question from one example competition in the web test data

SET @competition_id = (SELECT id FROM competition WHERE name="Photonics for All");
SET @question_id = (SELECT id FROM question WHERE competition_id=@competition_id AND question_setup_type="APPLICATION_TEAM");

DELETE from question_status WHERE question_id = @question_id;
DELETE from question WHERE id = @question_id;
