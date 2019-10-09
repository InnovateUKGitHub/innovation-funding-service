update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Annual turnover
        inner join form_input annual_turnover_input on annual_turnover_input.form_input_type_id = 27 and annual_turnover_input.description = 'Annual turnover' and annual_turnover_input.competition_id = comp.id
        inner join form_input_response annual_turnover_response on annual_turnover_response.form_input_id = annual_turnover_input.id and annual_turnover_response.application_id=app.id
        inner join process_role annual_turnover_updated_by on annual_turnover_updated_by.id = annual_turnover_response.updated_by_id and annual_turnover_updated_by.organisation_id = af.organisation_id

    SET gt.annual_turnover =           NULLIF(annual_turnover_response.value, '')

    where comp.include_project_growth_table = 1;