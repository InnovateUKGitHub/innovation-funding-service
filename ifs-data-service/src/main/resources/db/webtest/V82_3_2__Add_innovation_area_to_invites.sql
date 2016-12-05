-- gives an innovation area to each competition invite for non-existing users
UPDATE invite i
  SET i.innovation_category_id = (SELECT id FROM category WHERE name='Digital Manufacturing' AND type='INNOVATION_AREA')
WHERE
  type='COMPETITION' AND NOT EXISTS (SELECT 1 FROM user u WHERE u.email=i.email);