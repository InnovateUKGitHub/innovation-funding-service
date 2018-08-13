-- IFS-2941 As an applicant I am only offered the Research category eligible for the competition
-- This script adds all research categories to all open competitions

SET @feasibility_category_id = (SELECT id FROM category WHERE type='RESEARCH_CATEGORY' AND name='Feasibility studies');
SET @industrial_category_id = (SELECT id FROM category WHERE type='RESEARCH_CATEGORY' AND name='Industrial research');
SET @experimental_category_id = (SELECT id FROM category WHERE type='RESEARCH_CATEGORY' AND name='Experimental development');

INSERT INTO category_link(class_name, class_pk, category_id)
  SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, @feasibility_category_id
  FROM milestone m1
  JOIN milestone m2 ON m1.competition_id = m2.competition_id
  JOIN competition c ON m1.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() <= m2.date AND m2.type='SUBMISSION_DATE'
  AND c.non_ifs=false
  AND NOT EXISTS(SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = @feasibility_category_id);

INSERT INTO category_link(class_name, class_pk, category_id)
  SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, @industrial_category_id
  FROM milestone m1
  JOIN milestone m2 ON m1.competition_id = m2.competition_id
  JOIN competition c ON m1.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() <= m2.date AND m2.type='SUBMISSION_DATE'
  AND c.non_ifs=false
  AND NOT EXISTS (SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = @industrial_category_id);

INSERT INTO category_link(class_name, class_pk, category_id)
  SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, @experimental_category_id
  FROM milestone m1
  JOIN milestone m2 ON m1.competition_id = m2.competition_id
  JOIN competition c ON m1.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() <= m2.date AND m2.type='SUBMISSION_DATE'
  AND c.non_ifs=false
  AND NOT EXISTS (SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = @experimental_category_id);