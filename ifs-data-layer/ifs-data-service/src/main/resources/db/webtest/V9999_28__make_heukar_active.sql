-- IFS-8638 Heukar type only active on test environments.
UPDATE competition_type SET active = 1 WHERE name = 'HEUKAR';