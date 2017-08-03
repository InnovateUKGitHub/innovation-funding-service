-- fixes typo in clazz_name
UPDATE form_validator SET
  clazz_name = 'org.innovateuk.ifs.validator.SignedLongIntegerValidator'
WHERE
  clazz_name = 'org.innovateuk.ifs.validator.SingedLongIntegerValidator';
