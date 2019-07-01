-- IFS-5989 -- Change the description of the terms and conditions section for all Expression of Interest competitions

SET @eoi_competition_type = (SELECT id FROM competition_type ct WHERE ct.name='Expression of interest');

UPDATE section s
    SET s.description = 'You are agreeing to these by submitting your application.'
WHERE
        s.section_type = 'TERMS_AND_CONDITIONS' AND
        s.competition_id IN (
            SELECT c.id
            FROM competition c
            WHERE
                    c.competition_type_id = @eoi_competition_type OR
                    c.template AND c.name = 'Template for the Expression of interest competition type'
    );