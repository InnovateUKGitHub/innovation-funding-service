-- IFS-3708 convert the test funders types to give them legitimate values.
UPDATE competition_funder SET funder = 'Advanced Propulsion Centre (APC)' WHERE funder = 'Season Services';
UPDATE competition_funder SET funder = 'Aerospace Technology Institute (ATI)' WHERE funder = 'Downstream Technologies';
UPDATE competition_funder SET funder = 'Centre for Connected and Autonomous Vehicles (CCAV)' WHERE funder = 'Asterix Technologies';
UPDATE competition_funder SET funder = 'Department for Business, Energy and Industrial Strategy (BEIS)' WHERE funder = 'Biology Industries';
UPDATE competition_funder SET funder = 'Department for Digital, Culture, Media and Sport (DCMS)' WHERE funder = 'Keeps Inc';
UPDATE competition_funder SET funder = 'European: Eureka, Eurostars and other EU' WHERE funder = 'Delta Dagger Industries';
UPDATE competition_funder SET funder = 'Industrial Strategy Challenge Fund (ISCF)' WHERE funder = 'Basis Works';
UPDATE competition_funder SET funder = 'Innovate UK core budget' WHERE funder = 'Snowbirds Global';
UPDATE competition_funder SET funder = 'Smart Open' WHERE funder = 'Downstream Technologies';
-- The following do not appear to be used so are all set to "Other stakeholders"
UPDATE competition_funder
   SET funder = 'Other stakeholders'
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