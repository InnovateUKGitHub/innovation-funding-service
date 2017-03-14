ALTER TABLE contract DROP FOREIGN KEY contract_created_by_to_user_fk;
ALTER TABLE contract DROP FOREIGN KEY contract_modified_by_to_user_fk;
ALTER TABLE profile DROP FOREIGN KEY profile_contract_to_contract_fk;
DROP INDEX contract_created_by_to_user_fk ON contract;
DROP INDEX contract_modified_by_to_user_fk ON contract;

ALTER TABLE profile CHANGE contract_id agreement_id BIGINT(20);
ALTER TABLE profile CHANGE contract_signed_date agreement_signed_date DATETIME;

ALTER TABLE profile MODIFY COLUMN agreement_id BIGINT(20) AFTER business_type;
ALTER TABLE profile MODIFY COLUMN agreement_signed_date DATETIME AFTER agreement_id;

RENAME TABLE contract TO agreement;

CREATE INDEX agreement_created_by_to_user_fk ON agreement (created_by);
CREATE INDEX agreement_modified_by_to_user_fk ON agreement (modified_by);
ALTER TABLE agreement ADD CONSTRAINT agreement_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id);
ALTER TABLE agreement ADD CONSTRAINT agreement_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id);
ALTER TABLE profile ADD CONSTRAINT profile_agreement_to_agreement_fk FOREIGN KEY (agreement_id) REFERENCES agreement (id);