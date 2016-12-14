# tom this should now be in the tes data

# -- gives an innovation area to each competition invite for non-existing users
# UPDATE invite i
#   SET i.innovation_category_id = (SELECT id FROM category WHERE name='Earth Observation' AND type='INNOVATION_AREA')
# WHERE
#   type='COMPETITION' AND NOT EXISTS (SELECT 1 FROM user u WHERE u.email=i.email);