-- re-name competition_user_status
RENAME TABLE competition_user_status TO participant_status;


-- add to competition_user
ALTER TABLE competition_user
  ADD COLUMN participant_status_id bigint(20);

-- migrate existing data
UPDATE competition_user u, participant_status s
  SET u.participant_status_id = s.id
  WHERE u.competition_user_status_name = s.name;

-- drop the old column
ALTER TABLE competition_user
  DROP FOREIGN KEY competition_user_to_competition_user_status_fk,
  DROP COLUMN competition_user_status_name;

-- add constraints to the new column
ALTER TABLE competition_user
    MODIFY participant_status_id bigint(20) NOT NULL,
    ADD CONSTRAINT competition_user_to_participant_status_fk
      FOREIGN KEY (participant_status_id) REFERENCES participant_status(id);


-- add to project_user
ALTER TABLE project_user
    ADD COLUMN participant_status_id bigint(20);

-- all project users are implicitly accepted
UPDATE project_user SET participant_status_id=2;

ALTER TABLE project_user
  MODIFY participant_status_id bigint(20) NOT NULL,
  ADD CONSTRAINT project_user_to_participant_status_fk
    FOREIGN KEY (participant_status_id) REFERENCES participant_status(id);