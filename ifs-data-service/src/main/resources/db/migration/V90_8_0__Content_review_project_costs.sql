-- Your Project Costs

-- Capital usage
UPDATE form_input f
INNER JOIN question q ON f.question_id=q.id
INNER JOIN section s ON q.section_id=s.id
SET
q.description='<p>You can claim the usage costs of capital assets you will buy for, or use on, your project.</p>'
WHERE s.name='Capital usage' AND f.form_input_type_id=6;

-- Labour
UPDATE question q
SET
q.description='If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.'
WHERE q.description='<p>If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.</p>'