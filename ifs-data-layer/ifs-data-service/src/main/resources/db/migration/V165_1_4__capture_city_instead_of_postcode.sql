-- Make changes so that the international address can be changed in PS without affecting the original application
ALTER TABLE application_finance
  ADD COLUMN international_location VARCHAR(255);

ALTER TABLE partner_organisation
  ADD COLUMN international_location VARCHAR(255);

