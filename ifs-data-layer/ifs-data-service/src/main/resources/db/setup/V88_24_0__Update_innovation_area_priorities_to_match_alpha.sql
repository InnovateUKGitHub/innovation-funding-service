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