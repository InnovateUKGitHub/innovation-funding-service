-- Content update on Your organisation section. See IFS-2424
UPDATE question
SET name = 'Size' WHERE name = 'Organisation size';

UPDATE form_input
SET description = 'Size' WHERE description = 'Organisation Size';

UPDATE organisation_size
SET description = 'Micro or small' WHERE description = 'Micro / small';