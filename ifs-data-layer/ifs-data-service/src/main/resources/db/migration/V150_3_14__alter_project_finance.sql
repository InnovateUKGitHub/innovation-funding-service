ALTER TABLE  project_finance
    ADD COLUMN growth_table_id bigint(20),
    ADD CONSTRAINT fk_project_finance_growth_table_id FOREIGN KEY (growth_table_id) REFERENCES growth_table (id),
    ADD COLUMN employees_and_turnover_id bigint(20),
    ADD CONSTRAINT fk_project_finance_employees_and_turnover_id FOREIGN KEY (employees_and_turnover_id) REFERENCES employees_and_turnover (id);

