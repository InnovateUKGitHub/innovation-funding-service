-- IFS-3195 rename organisation.company_house_number -> companies_house_number

-- 3. contract: IFS-4196 drop the old column
ALTER TABLE organisation DROP COLUMN company_house_number;