
-- Replace space in question title.
UPDATE question q
SET q.short_name='Public description'
WHERE q.short_name='Public description\n';


-- Replace space in question title.
UPDATE question q
SET q.short_name='Project summary'
WHERE q.short_name='Project summary\n';