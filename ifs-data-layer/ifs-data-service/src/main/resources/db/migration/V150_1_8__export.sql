update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Annual export
        inner join form_input annual_export_input on annual_export_input.form_input_type_id = 27 and annual_export_input.description = 'Annual export' and annual_export_input.competition_id = comp.id
        left join form_input_response annual_export_response on annual_export_response.form_input_id = annual_export_input.id and annual_export_response.application_id=app.id
        left join process_role annual_export_updated_by on annual_export_updated_by.id = annual_export_response.updated_by_id and annual_export_updated_by.organisation_id = af.organisation_id

    SET gt.annual_export =             NULLIF(annual_export_response.value, '')

    where comp.include_project_growth_table = 1;
