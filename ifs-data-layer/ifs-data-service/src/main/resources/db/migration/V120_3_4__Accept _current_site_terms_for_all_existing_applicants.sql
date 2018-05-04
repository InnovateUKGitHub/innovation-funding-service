-- IFS-3093 Accept the existing Site terms and conditions for all applicants

SET @site_terms_and_conditions_id = (SELECT id
                                     FROM terms_and_conditions
                                     WHERE type = 'SITE');

INSERT INTO user_terms_and_conditions (user_id, terms_and_conditions_id, accepted_date)
  SELECT DISTINCT
    u.id,
    @site_terms_and_conditions_id,
    '1970-01-01 00:00:01'
  FROM user u
    INNER JOIN user_role ur ON u.id = ur.user_id
    INNER JOIN role r ON ur.role_id = r.id
  WHERE r.name = 'applicant';