-- Removing Heukar T&Cs IFS-8638
DELETE FROM terms_and_conditions WHERE name = 'Horizon Europe UK Application Privacy Notice';

-- Removing competition typeemployees_and_turnover
SET @competition_type_id = (SELECT id FROM competition_type WHERE name = 'HEUKAR');
DELETE FROM assessor_count_option WHERE competition_type_id = @competition_type_id;
DELETE FROM competition_type WHERE name = 'HEUKAR';

-- Removing Heukar Partner Organisations IFS-8667
DROP TABLE heukar_partner_organisation;
