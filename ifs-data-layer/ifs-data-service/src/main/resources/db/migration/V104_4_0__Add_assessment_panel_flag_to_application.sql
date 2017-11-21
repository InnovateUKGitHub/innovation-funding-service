ALTER TABLE application ADD COLUMN in_assessment_panel BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX application_in_assessment_panel ON application(in_assessment_panel);