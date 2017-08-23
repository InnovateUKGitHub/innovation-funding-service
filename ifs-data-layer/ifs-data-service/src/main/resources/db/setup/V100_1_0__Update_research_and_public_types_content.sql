-- Update research type description
UPDATE `organisation_type` SET `description`='Higher education and organisations registered with Je-S.' WHERE `name`='Research';


-- Update public sector and RTO type name
UPDATE `organisation_type` SET `name`='Research and technology organisation (RTO)' WHERE `name`='Research and technology organisations (RTOs)';
UPDATE `organisation_type` SET `name`='Public sector, charity or non Je-S registered research organisation' WHERE `name`='Public sector organisation or charity';