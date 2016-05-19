UPDATE `cost` SET `item`='Project leader' WHERE `id`='96';
UPDATE `cost` SET `item`='Trees' WHERE `id`='97';
UPDATE `cost` SET `description`='Capital usage description', `quantity`='22' WHERE `id`='98';
UPDATE `cost` SET `cost`='10000', `description`='Project leading', `item`='Worth Internet Systems' WHERE `id`='99';
UPDATE `cost` SET `item`='Sprint retrospective' WHERE `id`='100';
UPDATE `cost` SET `description`='Project setup', `item`='10-2016' WHERE `id`='101';
UPDATE `cost` SET `description`='Other funding' WHERE `id`='102';

INSERT INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES ('99', '1', 'Netherlands');
INSERT INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES ('98', '2', 'New');
INSERT INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES ('98', '3', '10');
INSERT INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES ('98', '4', '12');
