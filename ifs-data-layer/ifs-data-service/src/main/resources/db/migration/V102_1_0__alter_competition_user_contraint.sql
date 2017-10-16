ALTER TABLE `competition_user` DROP FOREIGN KEY `competition_user_to_competition_fk`;
ALTER TABLE `competition_user` DROP INDEX `UK_role_user_competition`;

ALTER TABLE competition_user ADD CONSTRAINT UK_role_user_competition UNIQUE (
	competition_id,
	user_id,
	competition_role,
    invite_id
	);

ALTER TABLE competition_user
ADD CONSTRAINT competition_user_to_competition_fk
FOREIGN KEY (competition_id) REFERENCES competition(id);