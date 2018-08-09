-- IFS-2941 As an applicant I am only offered the Research category eligible for the competition
INSERT INTO category_link(class_name, class_pk, category_id)
    SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, 33
  FROM milestone m1
  LEFT OUTER JOIN milestone m2 ON m1.competition_id = m2.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() < m2.date AND m2.type='SUBMISSION_DATE'
  AND NOT EXISTS(SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = 33);

  INSERT INTO category_link(class_name, class_pk, category_id)
    SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, 34
  FROM milestone m1
  LEFT OUTER JOIN milestone m2 ON m1.competition_id = m2.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() < m2.date AND m2.type='SUBMISSION_DATE'
  AND NOT EXISTS (SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = 34);

  INSERT INTO category_link(class_name, class_pk, category_id)
    SELECT 'org.innovateuk.ifs.competition.domain.Competition#researchCategory', m1.competition_id, 35
  FROM milestone m1
  LEFT OUTER JOIN milestone m2 ON m1.competition_id = m2.competition_id
  WHERE NOW() > m1.date AND m1.type='OPEN_DATE'
  AND NOW() < m2.date AND m2.type='SUBMISSION_DATE'
  AND NOT EXISTS (SELECT * FROM category_link cl
                  WHERE cl.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
                  AND cl.class_pk = m1.competition_id
                  AND cl.category_id = 35);








