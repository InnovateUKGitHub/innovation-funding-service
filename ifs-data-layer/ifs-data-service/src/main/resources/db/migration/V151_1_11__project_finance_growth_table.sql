insert into growth_table (temp_project_finance_id, financial_year_end, annual_turnover, annual_profits, annual_export, research_and_development, employees)
    select pf.id                              as temp_project_finance_id,
           g.financial_year_end               as financial_year_end,
           g.annual_turnover                  as annual_turnover,
           g.annual_profits                   as annual_profits,
           g.annual_export                    as annual_export,
           g.research_and_development         as research_and_development,
           g.employees                        as employees
    from project_finance pf
        inner join project p on p.id = pf.project_id
        inner join application app on app.id = p.application_id
        inner join application_finance af on af.application_id = app.id and af.organisation_id = pf.organisation_id
        inner join growth_table g on g.temp_application_finance_id = af.id;
