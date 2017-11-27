-- Add status data to existing completed competitions so they are working as intended

-- Add Question setup statuses for existing completed competitions
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `parent_id`, `target_class_name`, `target_id`)
SELECT 1, 'org.innovateuk.ifs.application.domain.Question', q.id, pss.id, 'org.innovateuk.ifs.competition.domain.Competition', q.competition_id
FROM question q
    JOIN setup_status pss ON pss.class_name = 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection'
        AND pss.class_pk = 6
        AND pss.target_class_name = 'org.innovateuk.ifs.competition.domain.Competition'
        AND pss.target_id = q.competition_id
WHERE q.question_type = 'GENERAL'
AND q.competition_id IN
    (SELECT c.id FROM competition c WHERE setup_complete = 1);

-- Add setup status - Application_details for existing completed competitions
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `parent_id`, `target_class_name`, `target_id`)
SELECT 1, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection', 3, pss.id, 'org.innovateuk.ifs.competition.domain.Competition', c.id
FROM competition c
    JOIN setup_status pss ON pss.class_name = 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection'
        AND pss.class_pk = 6
        AND pss.target_class_name = 'org.innovateuk.ifs.competition.domain.Competition'
        AND pss.target_id = c.id
WHERE setup_complete = 1;

-- Add setup status - Finances for existing completed competitions
INSERT INTO `setup_status` (`completed`, `class_name`, `class_pk`, `parent_id`, `target_class_name`, `target_id`)
SELECT 1, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection', 4, pss.id, 'org.innovateuk.ifs.competition.domain.Competition', c.id
FROM competition c
JOIN setup_status pss ON pss.class_name = 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection'
        AND pss.class_pk = 6
        AND pss.target_class_name = 'org.innovateuk.ifs.competition.domain.Competition'
        AND pss.target_id = c.id
WHERE setup_complete = 1;




