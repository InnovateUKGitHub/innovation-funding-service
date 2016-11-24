ALTER TABLE `competition`
ADD COLUMN `full_application_finance`bit(1) NOT NULL DEFAULT 1,
ADD COLUMN `include_growth_table`bit(1) NOT NULL DEFAULT 1;