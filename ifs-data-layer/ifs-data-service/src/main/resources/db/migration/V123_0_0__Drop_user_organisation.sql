DROP TABLE user_organisation;

-- process_role user_id, role_id, and application_id cannot be null
-- this needs no writes to the db
ALTER TABLE process_role
  DROP FOREIGN KEY FK_gm7bql0vdig803ktf5pc5mo2b,
  MODIFY user_id BIGINT(20) NOT NULL;
ALTER TABLE process_role ADD CONSTRAINT fk_process_role_user FOREIGN KEY (user_id) REFERENCES user(id);

ALTER TABLE process_role
  DROP FOREIGN KEY FK_j0syxe9gnfpvde1f6mqtul154,
  MODIFY role_id BIGINT(20) NOT NULL;
ALTER TABLE process_role ADD CONSTRAINT fk_process_role_role FOREIGN KEY (role_id) REFERENCES role(id);

ALTER TABLE process_role
  DROP FOREIGN KEY FK_20gvkjd4xrjyspmlisrd50xbj,
  MODIFY role_id BIGINT(20) NOT NULL;
ALTER TABLE process_role ADD CONSTRAINT fk_process_role_organisation FOREIGN KEY (organisation_id) REFERENCES organisation(id);
