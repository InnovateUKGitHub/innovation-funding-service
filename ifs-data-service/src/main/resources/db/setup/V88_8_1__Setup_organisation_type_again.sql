UPDATE `organisation_type` SET `name`='Research and technology organisations', `parent_organisation_type_id`=NULL WHERE `name`='Non profit distributing Research & Technology Organisation (RTO)';

INSERT INTO `organisation_type` (`name`) VALUES ('Public sector organisation or charity');
SET @public_charity_id=LAST_INSERT_ID();

INSERT INTO `organisation_type` (`name`) VALUES ('Research and technology organisations (RTO\'s)');
SET @rto_id=LAST_INSERT_ID();

UPDATE `organisation_type` SET `parent_organisation_type_id`=@public_charity_id WHERE `name`='Public Sector';
UPDATE `organisation_type` SET `parent_organisation_type_id`=@public_charity_id WHERE `name`='Charity';

UPDATE `organisation_type` SET `parent_organisation_type_id`=@rto_id WHERE `name`='Research and technology organisations';
UPDATE `organisation_type` SET `parent_organisation_type_id`=@rto_id WHERE `name`='Catapult';

UPDATE organisation_type cot, (SELECT `id` FROM `organisation_type` WHERE `name`='Public Sector') pot SET cot.parent_organisation_type_id=pot.id WHERE cot.name='Public sector research establishment';