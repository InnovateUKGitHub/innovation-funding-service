INSERT setup_status (completed, class_name, class_pk, target_id, target_class_name)

SELECT
1                                                                            as completed,
'org.innovateuk.ifs.competition.resource.CompetitionSetupSection'            as class_name,
13                                                                           as class_pk,
c.id                                                                         as target_id,
'org.innovateuk.ifs.competition.domain.Competition'                          as target_class_name

FROM competition c
WHERE c.setup_complete = 1
AND NOT EXISTS (SELECT id FROM setup_status WHERE class_name = 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection' AND class_pk = 13 AND
target_id = c.id AND target_class_name = 'org.innovateuk.ifs.competition.domain.Competition');

INSERT setup_status (completed, class_name, class_pk, target_id, target_class_name)

SELECT
1                                                                            as completed,
'org.innovateuk.ifs.competition.resource.CompetitionSetupSection'            as class_name,
14                                                                           as class_pk,
c.id                                                                         as target_id,
'org.innovateuk.ifs.competition.domain.Competition'                          as target_class_name

FROM competition c
WHERE c.setup_complete = 1
AND NOT EXISTS (SELECT id FROM setup_status WHERE class_name = 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection' AND class_pk = 14 AND
target_id = c.id AND target_class_name = 'org.innovateuk.ifs.competition.domain.Competition');