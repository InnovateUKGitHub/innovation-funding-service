ALTER TABLE `question`
ADD COLUMN `short_name` VARCHAR(255) NULL DEFAULT NULL AFTER `name`;
UPDATE `question` SET `short_name`='1. Business opportunity' WHERE `id`='1';
UPDATE `question` SET `short_name`='2. Potential market' WHERE `id`='2';
UPDATE `question` SET `short_name`='3. Project exploitation' WHERE `id`='3';
UPDATE `question` SET `short_name`='4. Economic benefit' WHERE `id`='4';
UPDATE `question` SET `short_name`='5. Technical approach' WHERE `id`='5';
UPDATE `question` SET `short_name`='6. Innovation' WHERE `id`='6';
UPDATE `question` SET `short_name`='7. Risks' WHERE `id`='7';
UPDATE `question` SET `short_name`='8. Project team' WHERE `id`='8';
UPDATE `question` SET `short_name`='Application details' WHERE `id`='9';
UPDATE `question` SET `short_name`='Project summary\n' WHERE `id`='11';
UPDATE `question` SET `short_name`='Public description\n' WHERE `id`='12';
UPDATE `question` SET `short_name`='Scope' WHERE `id`='13';
UPDATE `question` SET `short_name`='9. Funding' WHERE `id`='15';
UPDATE `question` SET `short_name`='10. Adding value' WHERE `id`='16';
