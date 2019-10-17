insert into employees_and_turnover (temp_project_finance_id, turnover, employees)
    select pf.id                        as temp_project_finance_id,
           e.turnover                   as turnover,
           e.employees                  as employees
    from project_finance pf
        inner join project p on p.id = pf.project_id
        inner join application app on app.id = p.application_id
        inner join application_finance af on af.application_id = app.id and af.organisation_id = pf.organisation_id
        inner join employees_and_turnover e on e.temp_application_finance_id = af.id;
