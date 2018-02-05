ALTER TABLE application CHANGE in_assessment_panel in_assessment_review_panel BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE application
  DROP INDEX application_in_assessment_panel,
  ADD INDEX application_in_assessment_review_panel_idx (in_assessment_review_panel);
