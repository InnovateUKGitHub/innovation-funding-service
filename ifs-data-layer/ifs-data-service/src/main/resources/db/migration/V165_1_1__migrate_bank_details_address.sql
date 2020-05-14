-- Bank details is using the organisation_address table. There is no need seeing as the bank_details already contains
-- details of the organisation.

ALTER TABLE bank_details
  ADD COLUMN address_id bigint(20);

UPDATE bank_details bd
    JOIN organisation_address oa ON
        oa.id=bd.organisation_address_id
    SET bd.address_id = oa.address_id;

ALTER TABLE bank_details MODIFY COLUMN address_id bigint(20) NOT NULL;
ALTER TABLE bank_details ADD CONSTRAINT bank_details_to_address_id_fk FOREIGN KEY (address_id) REFERENCES address (id);
ALTER TABLE bank_details DROP FOREIGN KEY bank_details_to_organisation_address_fk;
ALTER TABLE bank_details DROP COLUMN organisation_address_id;

-- remove unused address types.
DELETE FROM organisation_address WHERE address_type_id in (3,4);
DELETE FROM address_type where id in (3,4);
