-- Adding a column to the application table
ALTER TABLE `application`
ADD COLUMN `no_innovation_area_applicable` BIT(1) NOT NULL DEFAULT 0 AFTER `previous_application_title`;
