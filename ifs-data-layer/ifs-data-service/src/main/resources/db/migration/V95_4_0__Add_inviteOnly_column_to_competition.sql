-- Add boolean invite_only columns to competition table
ALTER TABLE `public_content`
ADD COLUMN `invite_only` BIT(1) DEFAULT null;

UPDATE `public_content` SET `invite_only`=b'0'
