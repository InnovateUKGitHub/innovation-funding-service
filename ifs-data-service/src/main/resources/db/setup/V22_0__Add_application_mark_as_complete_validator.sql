/** New mark as complete validator for Application details section **/

INSERT IGNORE INTO `form_validator` (`id`, `clazz_name`, `title`) VALUES (4,'com.worth.ifs.validator.ApplicationMarkAsCompleteValidator','ApplicationMarkAsCompleteValidator');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id` ) VALUES (9, 4);


