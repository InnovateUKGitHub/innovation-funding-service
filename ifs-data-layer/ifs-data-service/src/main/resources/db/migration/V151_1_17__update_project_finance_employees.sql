UPDATE project_finance pf
    INNER JOIN employees_and_turnover e on e.temp_project_finance_id = pf.id
SET pf.employees_and_turnover_id = e.id;