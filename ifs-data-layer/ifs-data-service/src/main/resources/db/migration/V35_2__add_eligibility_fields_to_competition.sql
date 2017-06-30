
-- Add competition columns for eligibility in competition setup.
ALTER TABLE competition ADD COLUMN multi_stream tinyint(1) NOT NULL DEFAULT '0';
ALTER TABLE competition ADD COLUMN collaboration_level VARCHAR(255);
ALTER TABLE competition ADD COLUMN lead_applicant_type VARCHAR(255);
