INSERT  IGNORE INTO `form_validator` (`id`, `clazz_name`, `title`) VALUES (1,'com.worth.ifs.validator.EmailValidator','EmailValidator'),(2,'com.worth.ifs.validator.NotEmptyValidator','NotEmptyValidator');

INSERT  IGNORE INTO `form_type_form_validator` (`form_input_type_id`, `form_validator_id`) VALUES (2,2);