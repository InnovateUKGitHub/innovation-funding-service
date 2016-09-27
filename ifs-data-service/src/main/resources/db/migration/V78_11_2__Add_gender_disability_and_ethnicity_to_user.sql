ALTER TABLE user ADD COLUMN gender ENUM('MALE', 'FEMALE', 'NOT_STATED');
ALTER TABLE user ADD COLUMN disability ENUM('YES', 'NO', 'NOT_STATED');
ALTER TABLE user
  ADD COLUMN ethnicity_id BIGINT(20),
  ADD CONSTRAINT user_to_ethnicity_fk
    FOREIGN KEY (ethnicity_id) REFERENCES ethnicity(id);