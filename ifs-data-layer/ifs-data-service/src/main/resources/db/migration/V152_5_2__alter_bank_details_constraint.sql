-- IFS-6484 alter bank details constraints.

ALTER TABLE bank_details
DROP FOREIGN KEY bank_details_to_organisation_address_fk;

ALTER TABLE bank_details
ADD CONSTRAINT bank_details_to_organisation_address_fk
    FOREIGN KEY (organisation_address_id) REFERENCES organisation_address (id) ON DELETE CASCADE;