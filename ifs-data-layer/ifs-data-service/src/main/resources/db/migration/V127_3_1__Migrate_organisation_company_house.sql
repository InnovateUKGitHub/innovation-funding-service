-- rename organisation.company_house_number -> companies_house_number

-- 1. expand: duplicate the existing column
ALTER TABLE organisation ADD COLUMN companies_house_number VARCHAR(255);

-- 2. migrate: copy everything from the old column (written to by v0, v1 of the services) to the new column
-- UPDATE organisation o JOIN organisation o1 ON o.id=o1.id SET o.companies_house_number = o1.company_house_number;

-- 3. contract
-- ALTER TABLE organisation DROP COLUMN company_house_number;