-- IFS-3086: Updating all old competitions to match the newly added section Terms and conditions
-- 9 is the Terms and Conditions section id

INSERT INTO setup_status (completed, class_name, class_pk, target_id, target_class_name)
    SELECT 1, 'org.innovateuk.ifs.competition.resource.CompetitionSetupSection', '9', c.id, 'org.innovateuk.ifs.competition.domain.Competition'
  FROM competition c
  WHERE c.setup_complete=TRUE;