-- IFS-5266 Refactor notified flag to hang off eu_grant rather than eu_contact

ALTER TABLE `eu_contact`
DROP COLUMN `notified`;

ALTER TABLE `eu_grant`
ADD COLUMN `notified` BOOLEAN DEFAULT FALSE;