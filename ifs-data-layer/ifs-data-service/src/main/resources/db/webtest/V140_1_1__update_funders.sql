-- IFS-3708 convert the test funders types to give them legitimate values.
SET SQL_SAFE_UPDATES = 0;

UPDATE competition_funder SET funder = 'ADVANCED_PROPULSION_CENTRE_APC' WHERE funder = 'Season Services';
UPDATE competition_funder SET funder = 'AEROSPACE_TECHNOLOGY_INSTITUTE_ATI' WHERE funder = 'Downstream Technologies';
UPDATE competition_funder SET funder = 'CENTRE_FOR_CONNECTED_AND_AUTONOMOUS_VEHICLES_CCAV' WHERE funder = 'Asterix Technologies';
UPDATE competition_funder SET funder = 'DEPARTMENT_FOR_BUSINESS_ENERGY_AND_INDUSTRIAL_STRATEGY_BEIS' WHERE funder = 'Biology Industries';
UPDATE competition_funder SET funder = 'DEPARTMENT_FOR_DIGITAL_CULTURE_MEDIA_AND_SPORT_DCMS' WHERE funder = 'Keeps Inc';
UPDATE competition_funder SET funder = 'EUROPEAN_EUREKA_EUROSTARS_AND_OTHER_EU' WHERE funder = 'Delta Dagger Industries';
UPDATE competition_funder SET funder = 'INDUSTRIAL_STRATEGY_CHALLENGE_FUND_ISCF' WHERE funder = 'Basis Works';
UPDATE competition_funder SET funder = 'INNOVATE_UK_CORE_BUDGET' WHERE funder = 'Snowbirds Global';
UPDATE competition_funder SET funder = 'SMART_OPEN' WHERE funder = 'Downstream Technologies';
-- The following do not appear to be used so are all set to "Other stakeholders"
UPDATE competition_funder
   SET funder = 'OTHER_STAKEHOLDERS'
 WHERE funder
    IN ('Gold Palm',
        'Saffron Global',
        'Lumber Worldwide',
        'Pandora Industries',
        'Foundation Worldwide',
        'Checkerboard Industries',
        'Goldeneye Technologies',
        'Nosh Systems',
        'Jumping Project Services',
        'Bloc Inc',
        'Silver Scratch Industries');

SET SQL_SAFE_UPDATES = 1;