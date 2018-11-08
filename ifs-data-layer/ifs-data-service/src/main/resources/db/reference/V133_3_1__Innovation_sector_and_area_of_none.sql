-- IFS-4662
-- reference data should be identical in all environments
SET @none_sector_id = 60;
SET @none_area_id = 61;

INSERT INTO category (id, name, type, parent_id, description, priority)
  VALUES (@none_sector_id,  'None', 'INNOVATION_SECTOR',  NULL,             NULL, -1),
         (@none_area_id,    'None', 'INNOVATION_AREA',    @none_sector_id,  'Not applicable', -1);