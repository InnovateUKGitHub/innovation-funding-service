update competition_type set template_competition_id = 2 where name = 'Programme';
update competition_type set template_competition_id = 3 where name = 'Sector';
update competition_type set template_competition_id = 4 where name = 'Generic';
update competition_type set template_competition_id = 5 where name = 'Expression of interest';
update competition_type set template_competition_id = 6 where name = 'Advanced Propulsion Centre';
update competition_type set template_competition_id = 7 where name = 'Aerospace Technology Institute';
update competition_type set template_competition_id = 8 where name = "The Prince's Trust";
update competition_type set template_competition_id = 9 where name = 'Horizon 2020';

-- Script for adding competition_user records for already set innovation leads during competition setup on existing competitions (IFS-191)
INSERT INTO competition_user (competition_id, competition_role, user_id, participant_status_id, type)
    SELECT id, 'INNOVATION_LEAD', lead_technologist_user_id, 2, 'INNOVATION_LEAD'
	FROM competition c
	WHERE c.lead_technologist_user_id IS NOT NULL
    AND NOT EXISTS (SELECT * FROM competition_user cu WHERE cu.competition_role = 'INNOVATION_LEAD' AND cu.competition_id = c.id AND cu.user_id = c.lead_technologist_user_id);