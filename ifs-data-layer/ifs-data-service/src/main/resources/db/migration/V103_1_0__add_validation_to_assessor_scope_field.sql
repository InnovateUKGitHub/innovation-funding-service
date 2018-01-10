INSERT INTO `form_validator` (`clazz_name`, `title`) VALUES ('org.innovateuk.ifs.validator.AssessorScopeValidator', 'AssessorScopeValidator');

INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) SELECT fi.id, fv.id FROM form_input fi
                                                                                  CROSS JOIN form_validator fv
                                                                                  WHERE fi.form_input_type_id IN
                                                                                    (SELECT id FROM form_input_type WHERE name = 'ASSESSOR_APPLICATION_IN_SCOPE')
                                                                                  AND fv.title = 'AssessorScopeValidator';