-- insert validators for template documents

INSERT INTO form_input_validator (form_input_id, form_validator_id)

SELECT fi.id                 as form_input_id,
       10                    as form_validator_id
FROM form_input fi
WHERE fi.form_input_type_id = 29;