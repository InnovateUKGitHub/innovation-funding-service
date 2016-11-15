
-- Programme
INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Programme'),'1', '1', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Programme'),'3', '3', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Programme'),'5', '5', true);

-- Additive Manufacturing
INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Additive Manufacturing'),'1', '1', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Additive Manufacturing'),'3', '3', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Additive Manufacturing'),'5', '5', true);

-- SBRI
INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'SBRI'),'1', '1', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'SBRI'),'3', '3', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'SBRI'),'5', '5', true);

-- Special
INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Special'),'1', '1', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Special'),'3', '3', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Special'),'5', '5', true);

-- Sector
INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Sector'),'1', '1', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Sector'),'3', '3', false);

INSERT INTO `competition_type_assessor_option` (`competition_type_id`, `assessor_option_name`, `assessor_option_value`,`default_option`)
VALUES ((SELECT id from competition_type where name = 'Sector'),'5', '5', true);