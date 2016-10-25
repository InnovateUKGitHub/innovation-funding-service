/* Following two updates added to allow marking and unmarking of Organisation Size and Funding Level on finance section (INFUND-446) */
UPDATE `question` SET `mark_as_completed_enabled`=1 WHERE `id`='38';
UPDATE `question` SET `mark_as_completed_enabled`=1 WHERE `id`='40';
UPDATE `question` SET `mark_as_completed_enabled`=1 WHERE `id`='20';
