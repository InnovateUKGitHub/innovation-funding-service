-- IFS-5344: Capture information about when notification is sent

ALTER TABLE `eu_grant`
ADD COLUMN `notified_on` DATETIME;