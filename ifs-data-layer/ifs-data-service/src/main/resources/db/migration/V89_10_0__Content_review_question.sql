-- Your Project Costs

-- Subcontracting costs
UPDATE form_input f
INNER JOIN question q ON f.question_id=q.id
INNER JOIN section s ON q.section_id=s.id
SET
f.guidance_answer='<p>Subcontracting costs relate to work carried out by third party organisations. These organisations are not part of your project.</p><p>Subcontracting is eligible providing it’s justified as to why the work cannot be performed by a project partner.</p><p>Subcontracting associate companies should be charged at cost.</p><p>Where possible you should select a UK based contractor. You should name the subcontractor (where known) and describe what they will be doing. You should also state where the work will be undertaken. We will look at the size of this contribution when assessing your eligibility and level of support.</p>'
WHERE s.name='Subcontracting costs' AND f.form_input_type_id=6;

-- Travel and subsistence
UPDATE form_input f
INNER JOIN question q ON f.question_id=q.id
INNER JOIN section s ON q.section_id=s.id
SET
q.description='<p>You should include travel and subsistence costs that relate to this project. </p>'
WHERE s.name='Travel and subsistence' AND f.form_input_type_id=6;