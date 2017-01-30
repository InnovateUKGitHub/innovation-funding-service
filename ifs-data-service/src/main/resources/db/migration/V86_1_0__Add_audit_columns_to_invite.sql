ALTER TABLE invite
	ADD COLUMN `sent_by` BIGINT(20) NULL,
    ADD CONSTRAINT invite_sent_by_to_user_fk FOREIGN KEY (sent_by) REFERENCES user(id),

    ADD COLUMN `sent_on` DATETIME NULL;
