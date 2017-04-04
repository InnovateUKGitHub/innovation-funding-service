-- Your Project Costs

-- Subcontracting costs
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
f.guidance_answer='<p>Subcontracting costs relate to work carried out by third party organisations. These organisations are not part of your project.</p><p>Subcontracting is eligible providing it’s justified as to why the work cannot be performed by a project partner.</p><p>Subcontracting associate companies should be charged at cost.</p><p>Where possible you should select a UK based contractor. You should name the subcontractor (where known) and describe what they will be doing. You should also state where the work will be undertaken. We will look at the size of this contribution when assessing your eligibility and level of support.</p>'
where s.name='Subcontracting costs' and f.form_input_type_id=6;

-- Travel and subsistence
update form_input f
inner join question q on f.question_id=q.id
inner join section s on q.section_id=s.id
set
q.description='<p>You should include travel and subsistence costs that relate to this project. </p>'
where s.name='Travel and subsistence' and f.form_input_type_id=6;
