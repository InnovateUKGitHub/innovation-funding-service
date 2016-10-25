/* Account number score has been dropped from SIL verification results schema, this is done already during validation phase */
ALTER TABLE bank_details DROP COLUMN account_number_score;

/* A field to specify experian validation was performed successfully and results added to table */
ALTER TABLE bank_details ADD COLUMN verified BIT(1) DEFAULT NULL;

/* These fields are now boolean in SIL schema, so refactore to BIT */
ALTER TABLE bank_details CHANGE `company_number_score` `registration_number_matched` BIT(1) DEFAULT NULL;
ALTER TABLE bank_details MODIFY `manual_approval` BIT(1) DEFAULT NULL;

CREATE TABLE `verification_condition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `severity` varchar(255) DEFAULT NULL,
  `code` int(11) DEFAULT NULL,
  `description` longtext,
  `bank_details_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_condition_bank_details_idx` (`bank_details_id`),
  CONSTRAINT `fk_condition_bank_details` FOREIGN KEY (`bank_details_id`) REFERENCES `bank_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
