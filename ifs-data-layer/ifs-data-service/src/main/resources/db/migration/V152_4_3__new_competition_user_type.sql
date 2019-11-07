-- IFS-6011

-- Add discriminator value for competition_user
ALTER TABLE competition_user ADD COLUMN type ENUM('ASSESSOR', 'INNOVATION_LEAD', 'INTERVIEW_PARTICIPANT', 'REVIEW_PARTICIPANT',
'STAKEHOLDER');

UPDATE competition_user SET type = 'ASSESSOR'
WHERE competition_role = 'ASSESSOR';

UPDATE competition_user SET type = 'INNOVATION_LEAD'
WHERE competition_role = 'INNOVATION_LEAD';

UPDATE competition_user SET type = 'STAKEHOLDER'
WHERE competition_role = 'STAKEHOLDER';

UPDATE competition_user SET type = 'INTERVIEW_PARTICIPANT'
WHERE competition_role = 'INTERVIEW_ASSESSOR';

UPDATE competition_user SET type = 'REVIEW_PARTICIPANT'
WHERE competition_role = 'PANEL_ASSESSOR';

ALTER TABLE competition_user MODIFY COLUMN type ENUM('ASSESSOR', 'INNOVATION_LEAD', 'STAKEHOLDER', 'INTERVIEW_PARTICIPANT', 'REVIEW_PARTICIPANT') NOT NULL;