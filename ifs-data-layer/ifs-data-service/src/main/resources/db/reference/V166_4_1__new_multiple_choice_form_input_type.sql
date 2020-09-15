-- Remove unused for input types
delete from form_input_type
    where id not in (2,4,21,22,23,29);

-- Insert new multiple choice form input type.
insert into form_input_type (id, name)
    VALUES (30, 'MULTIPLE_CHOICE');

-- Insert new validator for multiple choice questions.
insert into form_validator (id, clazz_name, title)
 VALUES (11, 'org.innovateuk.ifs.application.validator.RequiredMultipleChoiceValidator', 'RequiredMultipleChoiceValidator');

