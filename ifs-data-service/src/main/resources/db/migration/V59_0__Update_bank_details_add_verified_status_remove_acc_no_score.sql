/* Account number score has been dropped from SIL verification results schema, this is done already during validation phase */
ALTER TABLE bank_details DROP COLUMN account_number_score;

/* A field to specify experian validation was performed successfully and results added to table */
ALTER TABLE bank_details ADD COLUMN verified BIT(1) DEFAULT NULL;

/* These fields are now boolean in SIL schema, so refactore to BIT */
ALTER TABLE bank_details CHANGE `company_number_score` `registration_number_matched` BIT(1) DEFAULT NULL;
ALTER TABLE bank_details MODIFY `manual_approval` BIT(1) DEFAULT NULL;
