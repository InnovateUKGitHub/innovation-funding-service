
insert into employees_and_turnover (temp_application_finance_id)
    select af.id     as temp_application_finance_id
    from application_finance af
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        where comp.include_project_growth_table = 0;

update employees_and_turnover e
        inner join application_finance af on e.temp_application_finance_id = af.id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Turnover
        inner join form_input turnover_input on turnover_input.form_input_type_id = 24 and turnover_input.competition_id = comp.id
        left join form_input_response turnover_response on turnover_response.form_input_id = turnover_input.id and turnover_response.application_id=app.id
        left join process_role turnover_updated_by on turnover_updated_by.id = turnover_response.updated_by_id and turnover_updated_by.organisation_id = af.organisation_id
        -- Employees
        inner join form_input employees_input on employees_input.form_input_type_id = 25 and employees_input.competition_id = comp.id
        left join form_input_response employees_response on employees_response.form_input_id = employees_input.id and employees_response.application_id=app.id
        left join process_role employees_updated_by on employees_updated_by.id = employees_response.updated_by_id and employees_updated_by.organisation_id = af.organisation_id

    SET e.turnover = NULLIF(turnover_response.value, ''),
        e.employees = NULLIF(employees_response.value, '')

    where comp.include_project_growth_table = 0;