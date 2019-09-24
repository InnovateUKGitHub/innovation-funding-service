update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Annual profits
        inner join form_input annual_profits_input on annual_profits_input.form_input_type_id = 27 and annual_profits_input.description = 'Annual profits' and annual_profits_input.competition_id = comp.id
        left join form_input_response annual_profits_response on annual_profits_response.form_input_id = annual_profits_input.id and annual_profits_response.application_id=app.id
        left join process_role annual_profits_updated_by on annual_profits_updated_by.id = annual_profits_response.updated_by_id and annual_profits_updated_by.organisation_id = af.organisation_id

    SET gt.annual_profits =            NULLIF(annual_profits_response.value, '')

    where comp.include_project_growth_table = 1;
