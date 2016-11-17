-- Programme
INSERT INTO `assessor_count_option` (`competition_type_id`, `option_name`, `option_value`,`default_option`)
VALUES
((SELECT id from competition_type where name = 'Programme'),'1', '1', false),
((SELECT id from competition_type where name = 'Programme'),'3', '3', false),
((SELECT id from competition_type where name = 'Programme'),'5', '5', true),
-- Additive Manufacturing
((SELECT id from competition_type where name = 'Additive Manufacturing'),'1', '1', false),
((SELECT id from competition_type where name = 'Additive Manufacturing'),'3', '3', false),
((SELECT id from competition_type where name = 'Additive Manufacturing'),'5', '5', true),
-- SBRI
((SELECT id from competition_type where name = 'SBRI'),'1', '1', false),
((SELECT id from competition_type where name = 'SBRI'),'3', '3', false),
((SELECT id from competition_type where name = 'SBRI'),'5', '5', true),
-- Special
((SELECT id from competition_type where name = 'Special'),'1', '1', false),
((SELECT id from competition_type where name = 'Special'),'3', '3', false),
((SELECT id from competition_type where name = 'Special'),'5', '5', true),
-- Sector
((SELECT id from competition_type where name = 'Sector'),'1', '1', false),
((SELECT id from competition_type where name = 'Sector'),'3', '3', false),
((SELECT id from competition_type where name = 'Sector'),'5', '5', true);