SET foreign_key_checks = 0;

UPDATE application_finance af
    INNER JOIN employees_and_turnover e on e.temp_application_finance_id = af.id
SET af.employees_and_turnover_id = e.id;

SET foreign_key_checks = 1;