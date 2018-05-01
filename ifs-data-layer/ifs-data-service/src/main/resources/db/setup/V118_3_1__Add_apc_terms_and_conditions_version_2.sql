-- IFS-3171: Add new version of APC terms and conditions

-- Add entry into terms and conditions table for the version 2 APC terms and conditions
INSERT INTO terms_and_conditions (name, template, version)
    VALUES ("APC Terms and Conditions v2", "apc-terms-and-conditions-v2", 2);

-- Update link from template APC competition to new version of terms and conditions so these become the default Ts&Cs
--  used for new competitions of APC type.
SET @apc_terms_and_conditions_id=(SELECT id FROM terms_and_conditions WHERE name='APC Terms and Conditions v2');
SET @apc_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Advanced Propulsion Centre');

UPDATE competition SET terms_and_conditions_id=@apc_terms_and_conditions_id WHERE id=@apc_template_id;
