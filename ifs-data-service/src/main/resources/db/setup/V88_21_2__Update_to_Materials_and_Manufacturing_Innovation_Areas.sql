-- INFUND-8817 Update Materials and Manufacturing Innovation Areas

SELECT
  @materials_and_manufacturing_category := id
FROM category
WHERE
  `name` = 'Materials and manufacturing' AND
  `type` = 'INNOVATION_SECTOR';

UPDATE category
SET
  `name` = 'Digital manufacturing',
  `description` = 'Process analysis and control technologies including digital, sensor technology and metrology.',
  `priority` = 6
WHERE
  `name` = 'Digital Manufacturing' AND
  `type` = 'INNOVATION_AREA' AND
  `parent_id` = @materials_and_manufacturing_category;

UPDATE category
SET
  `name` = 'Forming technologies',
  `description` = 'Mechanical processing of a starting material to a final desired shape.',
  `priority` = 8
WHERE
  `name` = 'Early Stage Manufacturing' AND
  `type` = 'INNOVATION_AREA' AND
  `parent_id` = @materials_and_manufacturing_category;

UPDATE category
SET
  `name` = 'Assembly / disassembly / joining',
  `description` = 'Technologies for joining materials, ease of assembly / disassembly.',
  `priority` = 2
WHERE
  `name` = 'Manufacturing Readiness' AND
  `type` = 'INNOVATION_AREA' AND
  `parent_id` = @materials_and_manufacturing_category;

UPDATE category
SET
  `name` = 'Materials, process and manufacturing design technologies',
  `description` = 'Exploitation of digital design and manufacturing technologies to improve productivity and connect across supply chains.',
  `priority` = 9
WHERE
  `name` = 'Resource efficiency' AND
  `type` = 'INNOVATION_AREA' AND
  `parent_id` = @materials_and_manufacturing_category;

UPDATE category c
SET
  `name` = 'Nanotechnology / nanomaterials',
  `description` = 'New innovative nano material options with commercial application.',
  `priority` = 11
WHERE
  `name` = 'Advanced Materials' AND
  `type` = 'INNOVATION_AREA' AND
  `parent_id` = @materials_and_manufacturing_category;

-- 14 in total, 5 renamed

-- 9 new innovation areas
INSERT INTO category
  (`type`, `parent_id`, `priority`, `name`, `description`)
VALUES
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 3, 'Ceramic / electronic / functional materials', 'Includes hybrid composites, multiphase structural materials.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 1, 'Additive layer manufacturing (ALM)',	'Exploitation of ALM to achieve cost savings and enable manufacture of complex or novel parts.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 4, 'Chemical / bio processes',	'Often a process involving a "wet" stage recovery, purification or synthesis.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 13, 'Surface engineering, coatings and thin films',	'Chemical or physical modification of a surface to achieve a desired behaviour. Improved or new materials.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 7, 'Electronic materials and manufacturing', 'Including sensors.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 14, 'Sustainable materials',	'Valorisation, remanufacture and reuse of waste materials by novel extraction and processing technologies.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 5, 'Composite materials', 'New composite materials with enhanced or new properties and performance.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 10, 'Metals / metallurgy',	'New alloys, manufacturing technologies and processing routes.'),
  ('INNOVATION_AREA', @materials_and_manufacturing_category, 12, 'Polymers and plastics', 'New formulations, manufacturing technologies and processing routes.');