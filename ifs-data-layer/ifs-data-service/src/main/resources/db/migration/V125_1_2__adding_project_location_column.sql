-- Add project location column to finance tables.
ALTER TABLE application_finance ADD project_location varchar(225) NULL;
ALTER TABLE project_finance ADD project_location varchar(225) NULL;