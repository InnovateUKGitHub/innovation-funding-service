-- Other funding
SET @academic_organisation_type_id = (SELECT id FROM organisation_type WHERE name='Research');

-- Insert default of NO other funding.
insert into finance_row (cost, description, item, quantity, name, target_id, question_id, row_type)
    select '0', 'Other Funding', 'No', '0', 'other-funding', finance.id, quest.id, 'ApplicationFinanceRow'
    from application_finance finance
    inner join organisation org
    on org.id = finance.organisation_id
    inner join application app
    on app.id = finance.application_id
    inner join question quest
    on quest.competition_id = app.competition
    where org.organisation_type_id=@academic_organisation_type_id
    and quest.name='Other funding';

-- Insert 100% funding requested for all completed research funding questions.
insert into finance_row (cost, description, item, quantity, name, target_id, question_id, row_type)
    select '0', 'Grant Claim', '', '100', 'grant-claim', finance.id, quest.id, 'ApplicationFinanceRow'
    from application_finance finance
    inner join organisation org
    on org.id = finance.organisation_id
    inner join application app
    on app.id = finance.application_id
    inner join question quest
    on quest.competition_id = app.competition
    where org.organisation_type_id=@academic_organisation_type_id
    and quest.name='Funding level'
    and exists (select id from question_status where marked_as_complete = 1 and application_id=app.id and question_id=quest.id);

-- If the question is not complete then insert null for grant claim.
insert into finance_row (cost, description, item, quantity, name, target_id, question_id, row_type)
    select '0', 'Grant Claim', ' ', NULL, 'grant-claim', finance.id, quest.id, 'ApplicationFinanceRow'
    from application_finance finance
    inner join organisation org
    on org.id = finance.organisation_id
    inner join application app
    on app.id = finance.application_id
    inner join question quest
    on quest.competition_id = app.competition
    where org.organisation_type_id=@academic_organisation_type_id
    and quest.name='Funding level'
    and not exists (select inner_row.id from finance_row inner_row where inner_row.target_id=finance.id and name='grant-claim');

-- Project Finance
insert into finance_row (cost, description, item, quantity, name, target_id, question_id, row_type)
    select '0', 'Other Funding', 'No', '0', 'other-funding', finance.id, quest.id, 'ProjectFinanceRow'
    from project_finance finance
    inner join organisation org
    on org.id = finance.organisation_id
    inner join question quest
    on quest.competition_id = app.competition
    where org.organisation_type_id=@academic_organisation_type_id
    and quest.name='Other funding';

insert into finance_row (cost, description, item, quantity, name, target_id, question_id, row_type)
    select '0', 'Grant Claim', '', '100', 'grant-claim', finance.id, quest.id, 'ProjectFinanceRow'
    from project_finance finance
    inner join organisation org
    on org.id = finance.organisation_id
    inner join question quest
    on quest.competition_id = app.competition
    where org.organisation_type_id=@academic_organisation_type_id
    and quest.name='Funding level'
