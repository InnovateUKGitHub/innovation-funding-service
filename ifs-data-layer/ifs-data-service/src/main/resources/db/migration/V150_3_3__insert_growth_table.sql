insert into growth_table (temp_application_finance_id)
    select af.id     as temp_application_finance_id
    from application_finance af
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        where comp.include_project_growth_table = 1;
