UPDATE `organisation_type` SET `visible_in_setup`=1 WHERE `name`='Business';
UPDATE `organisation_type` SET `visible_in_setup`=1 WHERE `name`='Research';
UPDATE `organisation_type` SET `visible_in_setup`=1 WHERE `name`='Research and technology organisations (RTOs)';
UPDATE `organisation_type` SET `visible_in_setup`=0 WHERE `name`='Public sector organisation or charity';


SET @business_id = (SELECT `id` FROM organisation_type WHERE `name` = 'Business');
SET @research_id = (SELECT `id` FROM organisation_type WHERE `name` = 'Research');

-- conversion to new table structure
INSERT INTO lead_applicant_type (competition_id, organisation_type_id)
SELECT id, @business_id
FROM competition
WHERE `lead_applicant_type` = 'EITHER';

INSERT INTO lead_applicant_type (competition_id, organisation_type_id)
SELECT id, @research_id
FROM competition
WHERE `lead_applicant_type` = 'EITHER';

INSERT INTO lead_applicant_type (competition_id, organisation_type_id)
SELECT id, @research_id
FROM competition
WHERE `lead_applicant_type` = 'RESEARCH';

INSERT INTO lead_applicant_type (competition_id, organisation_type_id)
SELECT id, @business_id
FROM competition
WHERE `lead_applicant_type` = 'BUSINESS';