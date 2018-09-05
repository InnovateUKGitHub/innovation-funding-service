-- IFS-4105 remove diversity questions

ALTER TABLE user
  DROP COLUMN gender,
  DROP COLUMN disability,
  DROP FOREIGN KEY user_to_ethnicity_fk,
  DROP COLUMN ethnicity_id;

DROP TABLE ethnicity;