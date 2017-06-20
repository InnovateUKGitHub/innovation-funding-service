-- add profile_id to user
ALTER TABLE user
  ADD COLUMN profile_id BIGINT(20) UNIQUE,
  ADD CONSTRAINT user_profile_to_profile_fk FOREIGN KEY (profile_id) REFERENCES profile(id);

-- copy the existing profile relationships from profile.user_id to user.profile_id
UPDATE user u, profile p SET u.profile_id = p.id WHERE p.user_id = u.id;

-- drop the profile.user_id column
ALTER TABLE profile
  DROP FOREIGN KEY profile_user_to_user_fk,
  DROP COLUMN user_id;