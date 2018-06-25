-- Add project location column to finance tables.
ALTER TABLE application_finance ADD work_postcode varchar(225) NULL;
ALTER TABLE project_finance ADD work_postcode varchar(225) NULL;