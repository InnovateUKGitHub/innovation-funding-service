-- IFS-4662
-- reference data should be identical in all environments
SET @none_sector_id = 60;
SET @none_area_id = 61;

INSERT INTO category (id, name, type, description, priority)
  VALUES (@none_sector_id,  'None', 'INNOVATION_SECTOR',  NULL, -1),
         (@none_area_id,    'None', 'INNOVATION_AREA',    'Not applicable', -1);