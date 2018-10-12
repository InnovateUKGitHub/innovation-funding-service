-- IFS-4221 Migrate all open competitions to have collaboration level SINGLE_OR_COLLABORATIVE
UPDATE competition
SET collaboration_level = 'SINGLE_OR_COLLABORATIVE'
WHERE collaboration_level != 'SINGLE_OR_COLLABORATIVE' AND (
  setup_complete = 0 OR id IN (
    SELECT competition_id
    FROM milestone
    WHERE type = 'SUBMISSION_DATE' AND date > NOW())
);