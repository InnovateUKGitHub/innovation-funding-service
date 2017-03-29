-- Update and add innovation areas.
-- Note that the original values were place holders and should not be in use. Thus we are free to rename.
UPDATE category
   SET `NAME` = "Advanced therapies",
       description = "Approaches to improve development of advanced therapy medicinal products (ATMPs)"
 WHERE `NAME` = "Advanced Therapies";

UPDATE category
   SET `NAME` = "Biosciences",
       description = "Bioscience technologies supporting innovation in the health, agriculture, food and life science sectors."
 WHERE `NAME` = "Bioscience";

UPDATE category
   SET `NAME` = "Diagnostics, medical technology and devices",
       description = "Innovation in diagnostics, medical devices and medical technology"
 WHERE `NAME` = "Medicines Technology";

UPDATE category
   SET `NAME` = "Digital health",
       description = "Digital solutions to track, manage and improve health, and improve efficiency in healthcare delivery"
 WHERE `NAME` = "Agri Productivity";

 UPDATE category
   SET `NAME` = "Independent living and wellbeing",
       description = "Approaches to improve and sustain wellbeing in later life"
 WHERE `NAME` = "Enhanced Food Quality";

UPDATE category
   SET `NAME` = "Precision medicine",
       description = "Approaches to better match patients with the most appropriate treatment"
 WHERE `NAME` = "Precision Medicine";

-- We've run out of areas to renane - insert the remaining.
INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Preclinical technologies and drug target discovery", "INNOVATION_AREA", sector.id, "Technologies and approaches to improve and accelerate new medicine's development", 0
  FROM category AS sector
 WHERE sector.`NAME` = "Health and life sciences";

INSERT INTO category (`NAME`, `type`, parent_id, description, priority)
SELECT "Therapeutic and medicine development", "INNOVATION_AREA", sector.id, "Development of innovative pharmaceuticals and biopharmaceuticals, including vaccines and antimicrobials", 0
  FROM category AS sector
 WHERE sector.`NAME` = "Health and life sciences";

-- Priority should be in alphabetical order.
UPDATE category to_update,
    (SELECT
            @index:=@index + 1 AS iterator,
            sub_query_category.id AS category_id
        FROM
            category AS sub_query_category,
            (SELECT @index:=0) AS another_table
        WHERE sub_query_category.type='INNOVATION_AREA'
        ORDER BY sub_query_category.NAME
    ) sub_query
SET to_update.priority = sub_query.iterator
WHERE to_update.id = sub_query.category_id;