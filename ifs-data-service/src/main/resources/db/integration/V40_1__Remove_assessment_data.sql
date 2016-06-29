-- Delete all existing Assessment data
DELETE FROM `process_outcome` WHERE `process_id` IN (1,2,3,7);
DELETE FROM `process` WHERE `process_type`='Assessment';