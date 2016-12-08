INSERT INTO finance_row (cost, description, item, quantity, name, target_id, question_id, application_row_id, row_type)
SELECT fr.cost, fr.description, fr.item, fr.quantity, fr.name, pf.id, fr.question_id, fr.id, 'ProjectFinanceRow'
FROM finance_row fr
JOIN application_finance af ON af.id = fr.target_id
JOIN application a ON a.id = af.application_id
JOIN project p ON p.application_id = a.id
JOIN project_finance pf ON pf.project_id = p.id AND pf.organisation_id = af.organisation_id
WHERE fr.row_type = 'ApplicationFinanceRow';

INSERT INTO finance_row_meta_value (finance_row_id, finance_row_meta_field_id, value)
SELECT pfr.id, afrmv.finance_row_meta_field_id, afrmv.value
FROM finance_row_meta_value afrmv
JOIN finance_row pfr ON pfr.application_row_id = afrmv.finance_row_id;