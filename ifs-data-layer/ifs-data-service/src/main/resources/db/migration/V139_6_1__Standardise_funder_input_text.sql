-- IFS-5496 Change all funder inputs to be a value from a standard list
SET SQL_SAFE_UPDATES = 0;

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder = 'The National Physical Laboratory (NPL), Laboratory of the Government Chemist (LGC), National Engineering Laboratory (NEL) and Science and Technology Facilities Council (STFC) ';

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder LIKE '%OLEV%';

UPDATE competition_funder
SET funder = 'Office for Life Sciences (OLS)'
WHERE funder = 'Office for Life Sciences';

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder LIKE '%NHS England%'

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder = 'Network Rail';

-- WHAT SHOULD N/A Be
-- UPDATE competition_funder
-- SET funder = ''
-- WHERE funder LIKE 'N/A';

UPDATE competition_funder
SET funder = 'Industrial Strategy Challenge Fund (ISCF)'
WHERE funder = 'ISCF/BEIS' OR 'ISCF - ORG' OR 'ISCF - Innovate UK' OR 'ISCF' OR 'Innovate UK (ISCF)';

UPDATE competition_funder
SET funder = 'Innovate UK core budget'
WHERE funder = 'Innovate UK';

UPDATE competition_funder
SET funder = 'Industrial Strategy Challenge Fund (ISCF)'
WHERE funder = 'Industrial Strategy Challenge Fund';

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder = 'Geospatial Commission';

UPDATE competition_funder
SET funder = 'European: Eureka, Eurostars and other EU'
WHERE funder = 'Eureka';

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder = 'Department for Transport';

UPDATE competition_funder
SET funder = 'Department for Digital, Culture, Media and Sport (DCMS)'
WHERE funder = 'DCMS';

UPDATE competition_funder
SET funder = 'Centre for Connected and Autonomous Vehicles (CCAV)'
WHERE funder = 'CCAV';

UPDATE competition_funder
SET funder = 'Other stakeholders'
WHERE funder = 'BEIS (OLS)';

UPDATE competition_funder
SET funder = 'Aerospace Technology Institute (ATI)'
WHERE funder = 'BEIS (ATI)';

UPDATE competition_funder
SET funder = 'Department for Business, Energy and Industrial Strategy (BEIS)'
WHERE funder = 'BEIS';

UPDATE competition_funder
SET funder = 'Other delivery partners'
WHERE funder = 'ATI (BEIS)' OR 'ATI (BEIS)';

UPDATE competition_funder
SET funder = 'Aerospace Technology Institute (ATI)'
WHERE funder = 'ATI';

UPDATE competition_funder
SET funder = 'Advanced Propulsion Centre (APC)'
WHERE funder = 'APC';

SET SQL_SAFE_UPDATES = 1;