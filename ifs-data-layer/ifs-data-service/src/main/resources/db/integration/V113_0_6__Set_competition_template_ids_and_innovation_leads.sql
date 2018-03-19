update competition_type set template_competition_id = 8 where name = 'Programme';
update competition_type set template_competition_id = 9 where name = 'Sector';
update competition_type set template_competition_id = 10 where name = 'Generic';
update competition_type set template_competition_id = 18 where name = 'Expression of interest';
update competition_type set template_competition_id = 19 where name = 'Advanced Propulsion Centre';
update competition_type set template_competition_id = 20 where name = 'Aerospace Technology Institute';
update competition_type set template_competition_id = 21 where name = "The Prince's Trust";

-- Script for adding competition_user records for already set innovation leads during competition setup on existing competitions (IFS-191)
INSERT INTO competition_user (competition_id, competition_role, user_id, participant_status_id)
	SELECT id, 'INNOVATION_LEAD', lead_technologist_user_id, 2
	FROM competition c
	WHERE c.lead_technologist_user_id IS NOT NULL
				AND NOT EXISTS (SELECT * FROM competition_user cu WHERE cu.competition_role = 'INNOVATION_LEAD' AND cu.competition_id = c.id AND cu.user_id = c.lead_technologist_user_id);