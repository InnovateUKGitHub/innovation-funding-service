-- Script for adding competition_user records for already set innovation leads during competition setup on existing competitions (IFS-191)
INSERT INTO competition_user (competition_id, competition_role, user_id, participant_status_id, type)
    SELECT id, 'INNOVATION_LEAD', lead_technologist_user_id, 2, 'INNOVATION_LEAD'
	FROM competition c
	WHERE c.lead_technologist_user_id IS NOT NULL
    AND NOT EXISTS (SELECT * FROM competition_user cu WHERE cu.competition_role = 'INNOVATION_LEAD' AND cu.competition_id = c.id AND cu.user_id = c.lead_technologist_user_id);