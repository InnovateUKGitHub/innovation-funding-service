-- IFS-9124 addition t and c on competition for subsidy control
ALTER TABLE `competition` ADD COLUMN `subsidy_control_terms_and_conditions_id` bigint(20) DEFAULT NULL AFTER `terms_and_conditions_id`;