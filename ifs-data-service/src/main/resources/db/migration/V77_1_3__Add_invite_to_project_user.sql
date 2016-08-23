ALTER TABLE project_user ADD COLUMN invite_id bigint(20);

ALTER TABLE project_user ADD CONSTRAINT project_user_to_invite_fk FOREIGN KEY (invite_id) REFERENCES invite(id);
