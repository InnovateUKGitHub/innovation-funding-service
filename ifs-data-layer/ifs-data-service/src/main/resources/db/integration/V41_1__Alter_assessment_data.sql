UPDATE `process` SET `status`='submitted' WHERE `id` IN (1,5);
UPDATE `process` SET `event`='recommend' WHERE `id`=5;
UPDATE `process` SET `status`='open' WHERE `id` IN (2,6);
UPDATE `process_outcome` SET `process_id`=5 WHERE `id`=2;
-- Delete Process roles for assessors where there exists no associated Assessment
DELETE FROM `process_role` WHERE `id` IN (15,18,19);