-- Update and add innovation areas.
-- Ordering in this file is alphabetical for the new names and the priority represents this.
UPDATE category
   SET `NAME` = "Connected transport",
       description = "Improving transport infrastructure. This includes balancing peak demands and connecting different transport modes.",
       priority = 0
 WHERE `NAME` = "Transport systems";

INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Energy efficiency", "INNOVATION_AREA", sector.id, "Improve energy end-use efficiency, for example, in buildings, domestic appliances, industrial processes or vehicles.", 1
  FROM category AS sector
 WHERE sector.`NAME` = "Infrastructure systems";

INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Energy - other", "INNOVATION_AREA", sector.id, "Any aspect of energy not included elsewhere. This includes innovations to create a low carbon, affordable and secure supply.", 2
  FROM category AS sector
 WHERE sector.`NAME` = "Infrastructure systems";

UPDATE category
   SET `NAME` = "Energy systems",
       description = "Innovations that can help to match and profile future energy supply and demand.",
       priority = 3
 WHERE `NAME` = "Energy Systems";

UPDATE category
   SET `NAME` = "Nuclear fission",
       description = "Improvements in civil nuclear, including decommissioning.",
       priority = 4
 WHERE `NAME` = "Nuclear";

UPDATE category
   SET `NAME` = "Offshore wind",
       description = "Innovations that will reduce the cost of delivering offshore wind.",
       priority = 5
WHERE `NAME` = "Offshore Renewable Energy";

UPDATE category
   SET `NAME` = "Smart infrastructure",
       description = "Digital solutions (including BIM) to improve whole-life performance, resilience and sustainability of built assets.",
       priority = 6
 WHERE `NAME` = "Infrastructure";

UPDATE category
   SET `NAME` = "Urban living",
       description = "Urban integrated outcomes to challenges in cities and urban areas.",
       priority = 7
 WHERE `NAME` = "Urban living";