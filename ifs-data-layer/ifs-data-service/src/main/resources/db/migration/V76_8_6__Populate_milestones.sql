DELETE FROM `milestone`;

ALTER TABLE `milestone` CHANGE name type VARCHAR(255);

ALTER TABLE `milestone` ADD UNIQUE `milestone_unique_competition_type`(`type`, `competition_id`);

INSERT INTO `milestone` (`date`,`type`,`competition_id`) select start_date, 'OPEN_DATE', id from competition;
INSERT INTO `milestone` (`date`,`type`,`competition_id`) select end_date, 'SUBMISSION_DATE', id from competition;
INSERT INTO `milestone` (`date`,`type`,`competition_id`) select assessment_end_date, 'FUNDERS_PANEL', id from competition;
INSERT INTO `milestone` (`date`,`type`,`competition_id`) select assessment_start_date, 'ASSESSOR_ACCEPTS', id from competition;
INSERT INTO `milestone` (`date`,`type`,`competition_id`) select assessor_feedback_date, 'ASSESSOR_DEADLINE', id from competition;
INSERT INTO `milestone` (`date`,`type`,`competition_id`) select funders_panel_end_date, 'NOTIFICATIONS', id from competition;

ALTER TABLE `competition` DROP COLUMN `start_date`;
ALTER TABLE `competition` DROP COLUMN `end_date`;
ALTER TABLE `competition` DROP COLUMN `assessment_end_date`;
ALTER TABLE `competition` DROP COLUMN `assessment_start_date`;
ALTER TABLE `competition` DROP COLUMN `assessor_feedback_date`;
ALTER TABLE `competition` DROP COLUMN `funders_panel_end_date`;

