-- IFS-3261: Add a new version of default Innovate UK terms and conditions

-- Add entry into terms and conditions table for the version 2 Default Innovate UK terms and conditions
INSERT INTO terms_and_conditions (name, template, version)
    VALUES ("Default Terms and Conditions v2", "default-terms-and-conditions-v2", 2);


-- Update link from template competition to new version of terms and conditions so these become the default Ts&Cs
--  used for new competitions of Sector, Programme, Generic and EOI type.
SET @default_terms_and_conditions_id=(SELECT id FROM terms_and_conditions WHERE name='Default Terms and Conditions v2');
SET @sector_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Sector');
SET @programme_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Programme');
SET @generic_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Generic');
SET @eoi_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Expression of interest');

UPDATE competition SET terms_and_conditions_id=@default_terms_and_conditions_id WHERE id=@sector_template_id OR
                                   id=@programme_template_id OR id=@generic_template_id OR id=@eoi_template_id;
