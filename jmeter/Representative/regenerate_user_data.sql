-- Useful query to regenerate the data in user_data.csv after a change in the database.

-- Add some indexes and shortern some varchar columns to make the query execute faster.
-- Execute the query in a local (non docker) instance otherwise it will take a very long time to return 
alter table question modify short_name varchar(63);
alter table question add index short_name (short_name);
alter table question add index short_name_competition_id (short_name, competition_id);
alter table question modify name varchar(63);
alter table question add index name (name);
alter table form_input modify description varchar(63);
alter table form_input add index description (description);
alter table user add index email (email);
alter table section add index name (name);

select concat_ws(',', u.email, 'Passw0rd', a.id, c.id, application_details_question.id, project_summary_question.id, public_description_question.id, project_exploitation_question.id, economic_benefit_question.id, funding_question.id, adding_value_question.id, your_finance_section.id, finance_overview_section.id, project_summary_form_input.id, public_description_form_input.id, project_exploitation_form_input.id, economic_benefit_form_input.id, funding_form_input.id, adding_value_form_input.id, travel_subsistence_row.id, travel_subsistence_question.id, other_funding_row.id, other_funding_question.id)
            from user u
            join process_role pr
              on pr.user_id = u.id
            join application a
              on a.id = pr.application_id
            join competition c
              on c.id = a.competition
            join question application_details_question
              on application_details_question.competition_id = c.id
             and application_details_question.short_name = 'Application details'
            join question project_summary_question
              on project_summary_question.competition_id = c.id
             and project_summary_question.short_name = 'Project summary'
            join question public_description_question
              on public_description_question.competition_id = c.id
             and public_description_question.short_name = 'Public description'
            join question project_exploitation_question
              on project_exploitation_question.competition_id = c.id
             and project_exploitation_question.short_name = 'Project exploitation'
            join question economic_benefit_question
              on economic_benefit_question.competition_id = c.id
             and economic_benefit_question.short_name = 'Economic benefit'
            join question funding_question
              on funding_question.competition_id = c.id
             and funding_question.short_name = 'Funding'
            join question adding_value_question
              on adding_value_question.competition_id = c.id
             and adding_value_question.short_name = 'Adding value'
            join question other_funding_question
              on other_funding_question.competition_id = c.id
             and other_funding_question.name = 'Other funding'
            join section your_finance_section
              on your_finance_section.competition_id = c.id
             and your_finance_section.name = 'Your finances'
            join question travel_subsistence_question
              on travel_subsistence_question.competition_id = c.id
             and travel_subsistence_question.name = 'Travel and subsistence'
            join section finance_overview_section
              on finance_overview_section.competition_id = c.id
             and finance_overview_section.name = 'Finances overview'
            join form_input project_summary_form_input
              on project_summary_form_input.question_id = project_summary_question.id
             and project_summary_form_input.description = 'Project summary'
            join form_input public_description_form_input
              on public_description_form_input.question_id = public_description_question.id
             and public_description_form_input.description = 'Public description'
            join form_input project_exploitation_form_input 
              on project_exploitation_form_input.question_id = project_exploitation_question.id
             and project_exploitation_form_input.description like '3. How will you exploit %'
            join form_input economic_benefit_form_input 
              on economic_benefit_form_input.question_id = economic_benefit_question.id
             and economic_benefit_form_input.description like '4. What economic, social %'
            join form_input funding_form_input
              on funding_form_input.question_id = funding_question.id
             and funding_form_input.description = '9. What will your project cost?'
            join form_input adding_value_form_input 
              on adding_value_form_input.question_id = adding_value_question.id
             and adding_value_form_input.description like '10. How does financial %'
            join application_finance travel_subsistence_finance
              on travel_subsistence_finance.application_id = a.id
            join finance_row travel_subsistence_row
              on travel_subsistence_row.question_id = travel_subsistence_question.id
             and travel_subsistence_row.target_id = travel_subsistence_finance.id
            join application_finance other_funding_finance
              on other_funding_finance.application_id = a.id
            join finance_row other_funding_row
              on other_funding_row.question_id = other_funding_question.id
             and other_funding_row.target_id = other_funding_finance.id
             and other_funding_row.description =  'Other Funding'
           where email in ('peter.styles@load.example.com',
                           'jessica.mason@load.example.com',
                           'malcom.jones@load.example.com', 
                           'bethany.timpson@load.example.com',
                           'derik.arnold@load.example.com',
                           'sarah.james@load.example.com',
                           'richard.williams@load.example.com',
                           'karen.smith@load.example.com',
                           'simon.lightfoot@load.example.com',
                           'mary.evans@load.example.com',
                           'mike.ericsson@load.example.com',
                           'helen.rhodes@load.example.com',
                           'david.wellington@load.example.com',
                           'felicity.jefferies@load.example.com')
            order by a.id
            INTO OUTFILE 'users-csv.csv' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';
