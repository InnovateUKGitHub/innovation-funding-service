ALTER TABLE employees_and_turnover
    DROP COLUMN temp_application_finance_id,
    DROP COLUMN temp_project_finance_id;

ALTER TABLE growth_table
    DROP COLUMN temp_application_finance_id,
    DROP COLUMN temp_project_finance_id;