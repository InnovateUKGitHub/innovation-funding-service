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

DELETE FROM organisation_address WHERE address_type_id in (3,4);
DELETE FROM address_type where id in (3,4);

RENAME TABLE organisation_address TO organisation_application_address;

ALTER TABLE organisation_application_address
  DROP INDEX UK_g3y4ooi9akaq8e98efgmljigm,
  ADD COLUMN application_id bigint(20),
  ADD COLUMN created_on DATETIME,
  ADD UNIQUE KEY UK_organisation_application_address (organisation_id, address_type_id, application_id);

INSERT INTO organisation_application_address (organisation_id, application_id, address_id, address_type_id, created_on)
    SELECT
        pr.organisation_id          AS organisation_id,
        pr.application_id           AS application_id,
        oaa.address_id              AS address_id,
        oaa.address_type_id         AS address_type_id,
        now()                       AS created_on
    FROM process_role pr
    INNER JOIN organisation_application_address oaa on oaa.organisation_id = pr.organisation_id
    WHERE pr.organisation_id IS NOT NULL
    GROUP BY pr.organisation_id, pr.application_id;

DELETE FROM organisation_application_address WHERE application_id IS NULL;

ALTER TABLE organisation_application_address MODIFY COLUMN application_id bigint(20) NOT NULL;
ALTER TABLE organisation_application_address ADD CONSTRAINT organisation_application_address_to_application_id FOREIGN KEY (application_id) REFERENCES application (id);
ALTER TABLE organisation_application_address MODIFY COLUMN created_on DATETIME NOT NULL;