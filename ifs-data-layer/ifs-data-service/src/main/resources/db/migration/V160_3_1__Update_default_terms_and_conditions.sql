-- IFS-7165 - Update to InnovateUK Grant T&Cs

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 28;
SET @sector_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Sector');
SET @programme_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Programme');
SET @generic_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Generic');
SET @eoi_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Expression of interest');

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Innovate UK', 'default-terms-and-conditions-v5', 5, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());

-- Update link from template competition to new version of terms and conditions so these become the default Ts&Cs
-- used for new competitions of Sector, Programme, Generic and EOI type.
UPDATE competition SET terms_and_conditions_id=@new_terms_id WHERE id=@sector_template_id OR
                                   id=@programme_template_id OR id=@generic_template_id OR id=@eoi_template_id;
