-- IFS-6192 Update the H2020 template to the latest Innovate UK terms

UPDATE competition
SET terms_and_conditions_id = (SELECT id FROM terms_and_conditions WHERE name='Innovate UK' ORDER BY version DESC LIMIT 1)
WHERE name = 'Template for the Horizon 2020 competition type' AND template;