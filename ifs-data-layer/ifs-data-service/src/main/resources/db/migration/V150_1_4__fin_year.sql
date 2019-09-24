update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Financial year end
        inner join form_input year_end_input on year_end_input.form_input_type_id = 26 and year_end_input.competition_id = comp.id
        left join form_input_response year_end_response on year_end_response.form_input_id = year_end_input.id and year_end_response.application_id=app.id
        left join process_role year_end_updated_by on year_end_updated_by.id = year_end_response.updated_by_id and year_end_updated_by.organisation_id = af.organisation_id

    SET gt.financial_year_end =        STR_TO_DATE(NULLIF(year_end_response.value, '-'), '%m-%Y')

    where comp.include_project_growth_table = 1;
