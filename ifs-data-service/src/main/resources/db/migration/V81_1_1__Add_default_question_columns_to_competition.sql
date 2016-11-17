ALTER TABLE `competition`
ADD COLUMN `use_project_title_question`bit(1) NOT NULL DEFAULT 1,
ADD COLUMN `use_resubmission_question`bit(1) NOT NULL DEFAULT 1,
ADD COLUMN `use_estimated_start_date_question`bit(1) NOT NULL DEFAULT 1,
ADD COLUMN `use_duration_question`bit(1) NOT NULL DEFAULT 1;