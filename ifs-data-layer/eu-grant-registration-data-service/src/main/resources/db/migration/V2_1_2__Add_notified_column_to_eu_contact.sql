-- IFS-5208 Add notified column to eu contacts table to keep track of who has been sent the invitation email
ALTER TABLE `eu_contact`
ADD COLUMN `notified` BOOLEAN DEFAULT FALSE;