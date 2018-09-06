-- IFS-2994 - Alter competition_user. Modify competition_role to add a new enum STAKEHOLDER

ALTER TABLE competition_user
MODIFY COLUMN competition_role ENUM('ASSESSOR', 'INNOVATION_LEAD', 'PANEL_ASSESSOR', 'INTERVIEW_ASSESSOR', 'STAKEHOLDER') NOT NULL;