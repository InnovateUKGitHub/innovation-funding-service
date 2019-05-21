-- IFS-5874
DELETE fir FROM form_input_response AS fir
INNER JOIN form_input fi ON fi.id = fir.form_input_id
INNER JOIN question q ON q.id = fi.question_id
INNER JOIN section s ON s.id = q.section_id
WHERE s.section_type='PROJECT_COST_FINANCES' OR s.name IN ('Labour',
'Overhead costs',
'Materials',
'Capital usage',
'Subcontracting costs',
'Travel and subsistence',
'Other costs');
