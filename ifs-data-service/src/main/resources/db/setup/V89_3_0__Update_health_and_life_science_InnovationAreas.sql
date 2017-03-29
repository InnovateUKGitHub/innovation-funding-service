-- Update and add innovation areas. Priority is in alphabetical order.
-- Note that the original values were place holders and should not be in use. Thus we are free to rename.
UPDATE category
   SET `NAME` = "Advanced therapies",
       description = "Approaches to improve development of advanced therapy medicinal products (ATMPs)",
       priority = 0
 WHERE `NAME` = "Advanced Therapies";

UPDATE category
   SET `NAME` = "Biosciences",
       description = "Bioscience technologies supporting innovation in the health, agriculture, food and life science sectors.",
       priority = 1
 WHERE `NAME` = "Bioscience";

UPDATE category
   SET `NAME` = "Diagnostics, medical technology and devices",
       description = "Innovation in diagnostics, medical devices and medical technology",
       priority = 2
 WHERE `NAME` = "Medicines Technology";

UPDATE category
   SET `NAME` = "Digital health",
       description = "Digital solutions to track, manage and improve health, and improve efficiency in healthcare delivery",
       priority = 3
 WHERE `NAME` = "Agri Productivity";

 UPDATE category
   SET `NAME` = "Independent living and wellbeing",
       description = "Approaches to improve and sustain wellbeing in later life",
       priority = 4
 WHERE `NAME` = "Enhanced Food Quality";

UPDATE category
   SET `NAME` = "Precision medicine",
       description = "Approaches to better match patients with the most appropriate treatment",
       priority = 5
 WHERE `NAME` = "Precision Medicine";

-- We've run out of areas to renane - insert the remaining.
INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Preclinical technologies and drug target discovery", "INNOVATION_AREA", sector.id, "Technologies and approaches to improve and accelerate new medicines' development", 6
  FROM category AS sector
 WHERE sector.`NAME` = "Health and life sciences";

INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Therapeutics and medicines development", "INNOVATION_AREA", sector.id, "Development of innovative pharmaceuticals and biopharmaceuticals, including vaccines and antimicrobials", 7
  FROM category AS sector
 WHERE sector.`NAME` = "Health and life sciences";