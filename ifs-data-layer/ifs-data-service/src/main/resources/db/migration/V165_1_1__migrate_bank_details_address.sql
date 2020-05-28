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

-- We can't remove the organisation_address_id column because DW expect it, although they don't use it. remove constraint and set defautl
ALTER TABLE bank_details DROP FOREIGN KEY bank_details_to_organisation_address_fk;
ALTER TABLE bank_details MODIFY COLUMN organisation_address_id bigint(20) NOT NULL DEFAULT 0;

-- remove unused PROJECT and BANK_DETAILS address types. These are now linked to the bank_details and project tables directly.
DELETE FROM organisation_address WHERE address_type_id in (3,4);
DELETE FROM address_type where id in (3,4);


