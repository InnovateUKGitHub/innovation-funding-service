-- IFS-2123 - Temporarily modify test data to allow testing for backwards compatability for this feature
-- Remove the new research category question from one example competition in the web test data which _doesn't_ have the
-- new application team view either

SET @competition_id = (SELECT id FROM competition WHERE name="Photonics for All");
SET @question_id = (SELECT id FROM question WHERE competition_id=@competition_id AND question_setup_type="RESEARCH_CATEGORY");

DELETE from question_status WHERE question_id = @question_id;
DELETE from question WHERE id = @question_id;

-- Remove the new research category question from one example competition in the web test data
-- For a competition which _does_ have the new application team view deployed.
SET @competition_id = (SELECT id FROM competition WHERE name="Photonics for Public");
SET @question_id = (SELECT id FROM question WHERE competition_id=@competition_id AND question_setup_type="RESEARCH_CATEGORY");

DELETE from question_status WHERE question_id = @question_id;
DELETE from question WHERE id = @question_id;

-- Remove the new research category question from one example competition in the web test data
-- For a competition which _does_ have the new application team view deployed.
SET @competition_id = (SELECT id FROM competition WHERE name="Photonics for RTO and Business");
SET @question_id = (SELECT id FROM question WHERE competition_id=@competition_id AND question_setup_type="RESEARCH_CATEGORY");

DELETE from question_status WHERE question_id = @question_id;
DELETE from question WHERE id = @question_id;
