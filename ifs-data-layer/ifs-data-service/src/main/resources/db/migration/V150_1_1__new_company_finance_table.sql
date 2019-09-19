
CREATE TABLE employees_and_turnover (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_finance_id bigint(20),
  project_finance_id bigint(20),
  turnover double,
  employees int(11),
  CONSTRAINT fk_employees_and_turnover_application_finance_id FOREIGN KEY (application_finance_id) REFERENCES application_finance(id),
  CONSTRAINT fk_employees_and_turnover_project_finance_id FOREIGN KEY (project_finance_id) REFERENCES project_finance(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE growth_table (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_finance_id bigint(20),
  project_finance_id bigint(20),
  financial_year_end date,
  annual_turnover double,
  annual_profits double,
  annual_export double,
  research_and_development double,
  employees int(11),
  CONSTRAINT fk_growth_table_application_finance_id FOREIGN KEY (application_finance_id) REFERENCES application_finance(id),
  CONSTRAINT fk_growth_table_project_finance_id FOREIGN KEY (project_finance_id) REFERENCES project_finance(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into employees_and_turnover (application_finance_id, turnover, employees)
    select af.id                        as application_finance_id,
           turnover_response.value      as turnover,
           employees_response.value     as employees
    from application_finance af
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
    where comp.include_project_growth_table = 0;


insert into growth_table (application_finance_id, financial_year_end, annual_turnover, annual_profits, annual_export, research_and_development, employees)
    select af.id                                                                                                            as application_finance_id,
           date(concat(substring(year_end_response.value, 4, 4), '-', substring(year_end_response.value, 1, 2), '-01'))     as financial_year_end,
           annual_turnover_response.value                                                                                   as annual_turnover,
           annual_profits_response.value                                                                                    as annual_profits,
           annual_export_response.value                                                                                     as annual_export,
           research_and_development_response.value                                                                          as research_and_development,
           employees_response.value                                                                                         as employees
    from application_finance af
        inner join application app on af.application_id = app.id
        inner join competition comp on app.competition = comp.id
        -- Financial year end
        inner join form_input year_end_input on year_end_input.form_input_type_id = 26 and year_end_input.competition_id = comp.id
        left join form_input_response year_end_response on year_end_response.form_input_id = year_end_input.id and year_end_response.application_id=app.id
        left join process_role year_end_updated_by on year_end_updated_by.id = year_end_response.updated_by_id and year_end_updated_by.organisation_id = af.organisation_id
        -- Employees
        inner join form_input employees_input on employees_input.form_input_type_id = 28 and employees_input.competition_id = comp.id
        left join form_input_response employees_response on employees_response.form_input_id = employees_input.id and employees_response.application_id=app.id
        left join process_role employees_updated_by on employees_updated_by.id = employees_response.updated_by_id and employees_updated_by.organisation_id = af.organisation_id
        -- Annual turnover
        inner join form_input annual_turnover_input on annual_turnover_input.form_input_type_id = 27 and annual_turnover_input.description = 'Annual turnover' and annual_turnover_input.competition_id = comp.id
        left join form_input_response annual_turnover_response on annual_turnover_response.form_input_id = annual_turnover_input.id and annual_turnover_response.application_id=app.id
        left join process_role annual_turnover_updated_by on annual_turnover_updated_by.id = annual_turnover_response.updated_by_id and annual_turnover_updated_by.organisation_id = af.organisation_id
        -- Annual turnover
        inner join form_input annual_profits_input on annual_profits_input.form_input_type_id = 27 and annual_profits_input.description = 'Annual profits' and annual_profits_input.competition_id = comp.id
        left join form_input_response annual_profits_response on annual_profits_response.form_input_id = annual_profits_input.id and annual_profits_response.application_id=app.id
        left join process_role annual_profits_updated_by on annual_profits_updated_by.id = annual_profits_response.updated_by_id and annual_profits_updated_by.organisation_id = af.organisation_id
        -- Annual turnover
        inner join form_input annual_export_input on annual_export_input.form_input_type_id = 27 and annual_export_input.description = 'Annual export' and annual_export_input.competition_id = comp.id
        left join form_input_response annual_export_response on annual_export_response.form_input_id = annual_export_input.id and annual_export_response.application_id=app.id
        left join process_role annual_export_updated_by on annual_export_updated_by.id = annual_export_response.updated_by_id and annual_export_updated_by.organisation_id = af.organisation_id
        -- Annual turnover
        inner join form_input research_and_development_input on research_and_development_input.form_input_type_id = 27 and research_and_development_input.description = 'Research and development spend' and research_and_development_input.competition_id = comp.id
        left join form_input_response research_and_development_response on research_and_development_response.form_input_id = research_and_development_input.id and research_and_development_response.application_id=app.id
        left join process_role research_and_development_updated_by on research_and_development_updated_by.id = research_and_development_response.updated_by_id and research_and_development_updated_by.organisation_id = af.organisation_id
    where comp.include_project_growth_table = 1;


