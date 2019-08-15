-- IFS-6236

-- rename 'Your finances' section to 'Your project finances'
UPDATE section
SET name = 'Your project finances'
WHERE name = 'Your finances';