-- Update research type description
UPDATE `organisation_type` SET `description`='Higher education and organisations registered with Je-S.' WHERE `name`='Research';

-- Update public sector type name
UPDATE `organisation_type` SET `name`='Public sector, charity or non Je-S research organisation' WHERE `name`='Public sector organisation or charity';