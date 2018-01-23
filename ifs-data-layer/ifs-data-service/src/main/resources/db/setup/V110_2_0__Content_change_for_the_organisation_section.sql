-- Content update on Your organisation section. See IFS-2424
UPDATE question
SET name = 'Size' WHERE name = 'Organisation size';

UPDATE form_input
SET description = 'Size' WHERE description = 'Organisation Size';

UPDATE form_input
SET guidance_title = 'To determine the level of funding you are eligible to receive please provide your business size using the <a href="http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm" target="_blank" rel="external">EU definition</a> for guidance.'
WHERE description = 'Size';

UPDATE organisation_size
SET description = 'Micro or small' WHERE description = 'Micro / small';