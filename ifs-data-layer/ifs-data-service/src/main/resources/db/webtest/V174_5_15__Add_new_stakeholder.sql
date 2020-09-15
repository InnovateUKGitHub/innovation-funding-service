-- IFS-2994: Add new stakeholder.
SET @new_stakeholder_user_id =
(SELECT id FROM user WHERE email = 'rayon.kevin@gmail.com');

SET @sustainable =
(SELECT id FROM competition WHERE name = 'Sustainable living models for the future');

SET @internet_of_things =
(SELECT id FROM competition WHERE name = 'Internet of Things');

SET @rolling_stock =
(SELECT id FROM competition WHERE name = 'Rolling stock future developments');

SET @machine_learning =
(SELECT id FROM competition WHERE name = 'Machine learning for transport infrastructure');

SET @generic =
(SELECT id FROM competition WHERE name = 'Generic innovation');

SET @photonics =
(SELECT id FROM competition WHERE name = 'Photonics for Research');

SET @biosciences =
(SELECT id FROM competition WHERE name = 'Biosciences round three: plastic recovery in the industrial sector');

insert into competition_user
(competition_id, competition_role, user_id, participant_status_id, type)
values
(@sustainable, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@internet_of_things, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@rolling_stock, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@machine_learning, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@generic, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@photonics, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER'),
(@biosciences, 'STAKEHOLDER', @new_stakeholder_user_id, 2, 'STAKEHOLDER');