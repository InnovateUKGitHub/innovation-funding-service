/* This patch should be removed with the next baseline - INFUND-7678 */

UPDATE category_link
  INNER JOIN user ON user.id = category_link.class_pk
  SET
    category_link.class_pk = user.profile_id,
    class_name = 'org.innovateuk.ifs.user.domain.Profile'
  WHERE
    category_link.class_name = 'org.innovateuk.ifs.user.domain.User'