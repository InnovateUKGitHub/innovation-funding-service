ALTER TABLE invite DROP KEY `FK_skcmllljwagey78x7lmt2n00c`;
ALTER TABLE invite DROP FOREIGN KEY `FK_skcmllljwagey78x7lmt2n00c`;

ALTER TABLE invite DROP KEY `UK_e5xbk2ld658t66m1rv60igb5s`;

ALTER TABLE invite DROP FOREIGN KEY `FK_hexhehvongoy5cqgpem81xs86`;
ALTER TABLE invite DROP KEY `FK_hexhehvongoy5cqgpem81xs86`;


ALTER TABLE invite ADD COLUMN `type` VARCHAR(255) DEFAULT NULL;

ALTER TABLE invite CHANGE COLUMN `application_id` `target_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE invite CHANGE COLUMN `invite_organisation_id` `owner_id` BIGINT(20) DEFAULT NULL;


ALTER TABLE invite ADD UNIQUE KEY `UK_unique_target_id_email` (type, target_id, email);
