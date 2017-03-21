INSERT INTO `form_validator` (`clazz_name`, `title`) VALUES ('org.innovateuk.ifs.validator.AssessorScoreValidator', 'AssessorScoreValidator');
INSERT INTO `form_validator` (`clazz_name`, `title`) VALUES ('org.innovateuk.ifs.validator.ResearchCategoryValidator', 'ResearchCategoryValidator');

INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) SELECT fi.id, fv.id FROM form_input fi
                                                                                  CROSS JOIN form_validator fv
                                                                                  WHERE fi.competition_id IN
                                                                                    (SELECT id FROM competition WHERE name IN ('Template for the Programme competition type', 'Template for the Sector competition type'))
                                                                                  AND fi.form_input_type_id IN
                                                                                    (SELECT id FROM form_input_type WHERE name = 'ASSESSOR_SCORE')
                                                                                  AND fv.title = 'AssessorScoreValidator';

INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) SELECT fi.id, fv.id FROM form_input fi
                                                                                  CROSS JOIN form_validator fv
                                                                                  WHERE fi.competition_id IN
                                                                                    (SELECT id FROM competition WHERE name IN ('Template for the Programme competition type', 'Template for the Sector competition type'))
                                                                                  AND fi.form_input_type_id IN
                                                                                    (SELECT id FROM form_input_type WHERE name = 'ASSESSOR_RESEARCH_CATEGORY')
                                                                                  AND fv.title = 'ResearchCategoryValidator';