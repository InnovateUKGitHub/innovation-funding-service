/* Populate the competition types. */
UPDATE `competition` SET `competition_type_id`='1' WHERE `id`='1';
UPDATE `competition` SET `competition_type_id`='2' WHERE `id`='2';
UPDATE `competition` SET `competition_type_id`='3' WHERE `id`='3';
UPDATE `competition` SET `competition_type_id`='4' WHERE `id`='4';
UPDATE `competition` SET `competition_type_id`='1' WHERE `id`='5';
UPDATE `competition` SET `competition_type_id`='2' WHERE `id`='6';

/* Change the category link so that there is not a duplicate INNOVATION_AREA category linked to the same competition. */
UPDATE `category_link` SET `category_id`='33' WHERE `id`='2';
