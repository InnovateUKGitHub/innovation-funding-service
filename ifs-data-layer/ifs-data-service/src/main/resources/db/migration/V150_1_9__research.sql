update ignore growth_table gt
        inner join application_finance af on af.id = gt.temp_application_finance_id
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Annual research
        inner join form_input research_and_development_input on research_and_development_input.form_input_type_id = 27 and research_and_development_input.description = 'Research and development spend' and research_and_development_input.competition_id = comp.id
        left join form_input_response research_and_development_response on research_and_development_response.form_input_id = research_and_development_input.id and research_and_development_response.application_id=app.id
        left join process_role research_and_development_updated_by on research_and_development_updated_by.id = research_and_development_response.updated_by_id and research_and_development_updated_by.organisation_id = af.organisation_id

    SET gt.research_and_development =  NULLIF(replace(research_and_development_response.value, ',', ''), '')

    where comp.include_project_growth_table = 1;
