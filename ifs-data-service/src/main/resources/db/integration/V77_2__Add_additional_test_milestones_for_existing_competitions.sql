--Adding the additional test milestone for existing test competitions

INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('BRIEFING_EVENT','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('ALLOCATE_ASSESSORS','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('ASSESSOR_BRIEFING','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('LINE_DRAW','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('ASSESSMENT_PANEL','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('PANEL_DATE','2016-03-15 09:00:00','1');
INSERT IGNORE INTO `milestone` (`type`, `DATE`, `competition_id`) VALUES ('RELEASE_FEEDBACK','2016-03-15 09:00:00','1');

INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('BRIEFING_EVENT','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('ALLOCATE_ASSESSORS','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('ASSESSOR_BRIEFING','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('LINE_DRAW','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('ASSESSMENT_PANEL','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('PANEL_DATE','7');
INSERT IGNORE INTO `milestone` (`type`, `competition_id`) VALUES ('RELEASE_FEEDBACK','7');