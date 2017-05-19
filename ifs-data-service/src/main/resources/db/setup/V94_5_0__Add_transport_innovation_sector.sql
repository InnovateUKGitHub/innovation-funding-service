-- ADD TRANSPORT INNOVATION_SECTOR
INSERT INTO `category` (`name`, `type`, `priority`) VALUES ('Transport', 'INNOVATION_SECTOR', 5);
SET @parent_id = LAST_INSERT_ID();
-- ADD TRANSPORT INNOVATION_AREAs
INSERT INTO `category` (`name`, `type`, `description`, `parent_id`) VALUES
('Energy and automotive', 'INNOVATION_AREA', 'Including vehicle to grid, bi-directional chargers, business models', @parent_id),
('Low carbon vehicles', 'INNOVATION_AREA', 'Low carbon vehicles', @parent_id),
('Marine transport', 'INNOVATION_AREA', 'Marine technologies', @parent_id),
('Other transport', 'INNOVATION_AREA', 'Innovation in other transport', @parent_id),
('Rail transport', 'INNOVATION_AREA', 'Innovation in rail', @parent_id),
('Connected and autonomous vehicles', 'INNOVATION_AREA', 'Connected and autonomous vehicles technology', @parent_id);

--Fix priorities
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

UPDATE category SET priority=1 WHERE name='Health and life sciences';
UPDATE category SET priority=2 WHERE name='Materials and manufacturing';
UPDATE category SET priority=3 WHERE name='Emerging and enabling';
UPDATE category SET priority=4 WHERE name='Infrastructure systems';