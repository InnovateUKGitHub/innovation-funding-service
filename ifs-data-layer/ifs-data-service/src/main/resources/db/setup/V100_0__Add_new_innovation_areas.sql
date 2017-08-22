-- FIND parent categories
SET @health_parent_id = (SELECT `id` FROM `category` WHERE `name` = 'Health and life sciences' AND `type` = 'INNOVATION_SECTOR');
SET @transport_parent_id = (SELECT `id` FROM `category` WHERE `name` = 'Transport' AND `type` = 'INNOVATION_SECTOR');

-- ADD NEW INNOVATION_AREAS
INSERT INTO `category` (`name`, `type`, `description`, `parent_id`) VALUES
('Agricultural productivity', 'INNOVATION_AREA', 'Approaches to improve all aspects of agricultural, horticultural and aquacultural productivity on farms.', @health_parent_id),
('Enhancing food quality', 'INNOVATION_AREA', 'Solutions to enhance the compositional quality, nutritional value, safety and provenance of food.', @health_parent_id),
('Aerospace', 'INNOVATION_AREA', 'Research and technology programmes to encourage the growth of the UK aerospace sector.', @transport_parent_id);

-- FIX priorities
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