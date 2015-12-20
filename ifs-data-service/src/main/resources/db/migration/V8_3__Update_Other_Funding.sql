UPDATE `question` SET `guidance_answer`='Please tell us if you have received, or will receive any other public sector funding for this project.' WHERE `id`='35';
INSERT INTO `cost` (`cost`, `description`, `item`, `application_finance_id`, `question_id`) VALUES ('0', 'Other Funding', 'Yes', '1', '35');
UPDATE `question` SET `mark_as_completed_enabled`=1 WHERE `id`='35';
