-- Make changes so that the international address can be changed in PS without affecting the original application
ALTER TABLE partner_organisation
  ADD COLUMN international_address_id bigint(20),
  ADD CONSTRAINT partner_organisations_to_international_address_id_fk FOREIGN KEY (international_address_id) REFERENCES address (id);

