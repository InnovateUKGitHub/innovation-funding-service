ALTER TABLE employees_and_turnover
    DROP COLUMN temp_application_finance_id,
    DROP COLUMN temp_project_finance_id;

ALTER TABLE growth_table
    DROP COLUMN temp_application_finance_id,
    DROP COLUMN temp_project_finance_id;

ALTER TABLE application_finance
    ADD CONSTRAINT fk_application_finance_growth_table_id FOREIGN KEY (growth_table_id) REFERENCES growth_table (id),
    ADD CONSTRAINT fk_application_finance_employees_and_turnover_id FOREIGN KEY (employees_and_turnover_id) REFERENCES employees_and_turnover (id);

ALTER TABLE project_finance
    ADD CONSTRAINT fk_project_finance_growth_table_id FOREIGN KEY (growth_table_id) REFERENCES growth_table (id),
    ADD CONSTRAINT fk_project_finance_employees_and_turnover_id FOREIGN KEY (employees_and_turnover_id) REFERENCES employees_and_turnover (id);
