UPDATE project_finance
SET viability = 'NOT_APPLICABLE'
WHERE organisation_id IN
    (SELECT id FROM organisation
     WHERE organisation_type_id =
        (SELECT id FROM organisation_type
        WHERE name = 'University (HEI)')
    );
