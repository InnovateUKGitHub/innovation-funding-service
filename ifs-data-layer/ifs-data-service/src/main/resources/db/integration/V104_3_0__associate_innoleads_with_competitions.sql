-- Adding innovation leads to existing competitions for testing innovation lead specific queries added in IFS-1308

INSERT INTO competition_user (competition_id, competition_role, user_id, participant_status_id) VALUES ('1', 'INNOVATION_LEAD', '52', 2);

-- Adding competition_user records for already set innovation leads
INSERT INTO competition_user (competition_id, competition_role, user_id, participant_status_id)
SELECT id, 'INNOVATION_LEAD', lead_technologist_user_id, 2
FROM competition c
WHERE c.lead_technologist_user_id IS NOT NULL
AND NOT EXISTS (SELECT * FROM competition_user cu WHERE cu.competition_role = 'INNOVATION_LEAD' AND cu.competition_id = c.id AND cu.user_id = c.lead_technologist_user_id);