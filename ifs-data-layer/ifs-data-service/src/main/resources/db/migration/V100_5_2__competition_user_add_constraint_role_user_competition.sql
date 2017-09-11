ALTER TABLE competition_user ADD CONSTRAINT UK_role_user_competition UNIQUE (
	competition_id,
	user_id,
	competition_role
	);