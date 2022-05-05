-- IFS-11682 - Direct award type only active on test environments.
UPDATE competition_type SET active = 1 WHERE name = 'Direct award';