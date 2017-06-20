-- Adding description column to the category table
ALTER TABLE `category`
ADD COLUMN `description` MEDIUMTEXT NULL DEFAULT NULL AFTER `parent_id`;
