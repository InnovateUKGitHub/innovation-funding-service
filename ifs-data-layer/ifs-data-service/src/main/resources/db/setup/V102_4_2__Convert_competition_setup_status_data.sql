-- Convert the Home section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 1, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'HOME';

-- Convert the initial details section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 2, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'INITIAL_DETAILS';

-- Convert the additional info section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 3, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'ADDITIONAL_INFO';

-- Convert the eligibility section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 4, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'ELIGIBILITY';

-- Convert the milestones section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 5, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'MILESTONES';

-- Convert the application form section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 6, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'APPLICATION_FORM';

-- Convert the assessors section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 7, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'ASSESSORS';

-- Convert the content section
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `target_class_name`, `target_id`)
SELECT css.status, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', 8, 'org.innovateuk.ifs.competition.domain.Competition', css.competition_id
FROM competition_setup_status css
WHERE css.section = 'CONTENT';

