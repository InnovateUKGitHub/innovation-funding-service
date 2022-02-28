-- IFS-10688 - Hesta type only active on test environments.
UPDATE competition_type SET active = 1 WHERE name = 'Horizon Europe Guarantee';