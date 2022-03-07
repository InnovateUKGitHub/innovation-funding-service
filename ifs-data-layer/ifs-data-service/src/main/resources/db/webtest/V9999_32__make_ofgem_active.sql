-- IFS-11341 - Ofgem type only active on test environments.
UPDATE competition_type SET active = 1 WHERE name = 'Ofgem';