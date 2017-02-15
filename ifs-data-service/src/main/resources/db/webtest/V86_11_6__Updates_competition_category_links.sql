/*
  This database patch should be deleted with the next re-baseline.

  Please ensure that `category_link` does not have any rows with `class_name` of
  'org.innovateuk.ifs.competition.domain.Competition' and they have been replaced
  by values from the following:

  'org.innovateuk.ifs.competition.domain.Competition#innovationArea',
  'org.innovateuk.ifs.competition.domain.Competition#innovationSector',
  'org.innovateuk.ifs.competition.domain.Competition#researchCategory',
 */

UPDATE category_link
INNER JOIN category ON category_link.category_id = category.id
SET category_link.class_name = 'org.innovateuk.ifs.competition.domain.Competition#innovationArea'
WHERE category.type = 'INNOVATION_AREA'
  AND category_link.class_name =  'org.innovateuk.ifs.competition.domain.Competition';

UPDATE category_link
INNER JOIN category ON category_link.category_id = category.id
SET category_link.class_name = 'org.innovateuk.ifs.competition.domain.Competition#innovationSector'
WHERE category.type = 'INNOVATION_SECTOR'
  AND category_link.class_name =  'org.innovateuk.ifs.competition.domain.Competition';

UPDATE category_link
INNER JOIN category ON category_link.category_id = category.id
SET category_link.class_name = 'org.innovateuk.ifs.competition.domain.Competition#researchCategory'
WHERE category.type = 'RESEARCH_CATEGORY'
  AND category_link.class_name =  'org.innovateuk.ifs.competition.domain.Competition';
