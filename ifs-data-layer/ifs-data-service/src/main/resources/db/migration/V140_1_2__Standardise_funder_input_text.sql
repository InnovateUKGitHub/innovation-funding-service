-- IFS-5496 Change all funder inputs to be a value from a standard list
SET SQL_SAFE_UPDATES = 0;

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder = 'The National Physical Laboratory (NPL), Laboratory of the Government Chemist (LGC), National Engineering Laboratory (NEL) and Science and Technology Facilities Council (STFC) ';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder LIKE '%OLEV%';

UPDATE competition_funder
SET funder = 'OFFICE_FOR_LIFE_SCIENCES_OLS'
WHERE funder = 'Office for Life Sciences';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder LIKE '%NHS England%';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder = 'Network Rail';

-- Only one competition with this and can go to this
UPDATE competition_funder
SET funder = 'INDUSTRIAL_STRATEGY_CHALLENGE_FUND_ISCF'
WHERE funder LIKE 'N/A';

UPDATE competition_funder
SET funder = 'INDUSTRIAL_STRATEGY_CHALLENGE_FUND_ISCF'
WHERE funder IN ('ISCF/BEIS','ISCF - ORG','ISCF - Innovate UK','ISCF','Innovate UK (ISCF)');

UPDATE competition_funder
SET funder = 'INNOVATE_UK_CORE_BUDGET'
WHERE funder = 'Innovate UK';

UPDATE competition_funder
SET funder = 'INDUSTRIAL_STRATEGY_CHALLENGE_FUND_ISCF'
WHERE funder = 'Industrial Strategy Challenge Fund';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder = 'Geospatial Commission';

UPDATE competition_funder
SET funder = 'EUROPEAN_EUREKA_EUROSTARS_AND_OTHER_EU'
WHERE funder = 'Eureka';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder = 'Department for Transport';

UPDATE competition_funder
SET funder = 'DEPARTMENT_FOR_DIGITAL_CULTURE_MEDIA_AND_SPORT_DCMS'
WHERE funder = 'DCMS';

UPDATE competition_funder
SET funder = 'CENTRE_FOR_CONNECTED_AND_AUTONOMOUS_VEHICLES_CCAV'
WHERE funder = 'CCAV';

UPDATE competition_funder
SET funder = 'OTHER_STAKEHOLDERS'
WHERE funder = 'BEIS (OLS)';

UPDATE competition_funder
SET funder = 'AEROSPACE_TECHNOLOGY_INSTITUTE_ATI'
WHERE funder = 'BEIS (ATI)';

UPDATE competition_funder
SET funder = 'DEPARTMENT_FOR_BUSINESS_ENERGY_AND_INDUSTRIAL_STRATEGY_BEIS'
WHERE funder = 'BEIS';

UPDATE competition_funder
SET funder = 'OTHER_DELIVERY_PARTNERS'
WHERE funder = 'ATI (BEIS)' OR 'ATI (BEIS)';

UPDATE competition_funder
SET funder = 'AEROSPACE_TECHNOLOGY_INSTITUTE_ATI'
WHERE funder = 'ATI';

UPDATE competition_funder
SET funder = 'ADVANCED_PROPULSION_CENTRE_APC'
WHERE funder = 'APC';

SET SQL_SAFE_UPDATES = 1;