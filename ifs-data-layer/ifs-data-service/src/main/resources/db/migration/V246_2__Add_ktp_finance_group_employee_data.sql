-- IFS-11211-add-domain-model-update-for-group-employee-data-for-ktp-finance
ALTER TABLE ktp_financial_year
    ADD COLUMN  corporate_group_employees int;

