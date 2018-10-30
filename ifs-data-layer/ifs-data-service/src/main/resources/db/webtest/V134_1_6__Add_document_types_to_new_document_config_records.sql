-- IFS-4459 Add new document types to new document config records

INSERT INTO `document_config_file_type`
(`document_config_id`, `file_type_id`)
SELECT `id` AS `document_config_id`,
(SELECT `id` FROM `file_type` WHERE `name` = 'PDF') AS `file_type_id`
FROM `document_config` WHERE `competition_id` =
(SELECT `id` FROM `competition` WHERE `name` = 'Integrated delivery programme - solar vehicles');