
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.EmailValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.EmailValidator';
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.NotEmptyValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.NotEmptyValidator';
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.WordCountValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.WordCountValidator';
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.NonNegativeLongIntegerValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.NonNegativeLongIntegerValidator';
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.SignedLongIntegerValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.SignedLongIntegerValidator';
UPDATE form_validator SET clazz_name = 'org.innovateuk.ifs.application.validator.PastMMYYYYValidator' WHERE clazz_name = 'org.innovateuk.ifs.validation.validator.PastMMYYYYValidator';