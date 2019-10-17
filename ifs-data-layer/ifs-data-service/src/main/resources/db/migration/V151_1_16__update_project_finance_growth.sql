UPDATE project_finance pf
    INNER JOIN growth_table g on g.temp_project_finance_id = pf.id
SET pf.growth_table_id = g.id;