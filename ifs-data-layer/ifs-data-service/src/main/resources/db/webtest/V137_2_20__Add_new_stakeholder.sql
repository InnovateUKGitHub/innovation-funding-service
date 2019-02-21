-- IFS-2994: Add new stakeholder.
SET @new_stakeholder_user_id =
(SELECT id FROM user WHERE email = 'rayon.kevin@gmail.com');

insert into competition_user
(competition_id, competition_role, user_id, participant_status_id)
values
(23, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(24, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(11, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(35, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(16, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(10, 'STAKEHOLDER', @new_stakeholder_user_id, 2),
(18, 'STAKEHOLDER', @new_stakeholder_user_id, 2);