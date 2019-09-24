update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Employees
        inner join form_input employees_input on employees_input.form_input_type_id = 28 and employees_input.competition_id = comp.id
        left join form_input_response employees_response on employees_response.form_input_id = employees_input.id and employees_response.application_id=app.id
        left join process_role employees_updated_by on employees_updated_by.id = employees_response.updated_by_id and employees_updated_by.organisation_id = af.organisation_id

    SET gt.employees =                 NULLIF(employees_response.value, '')

    where comp.include_project_growth_table = 1;
