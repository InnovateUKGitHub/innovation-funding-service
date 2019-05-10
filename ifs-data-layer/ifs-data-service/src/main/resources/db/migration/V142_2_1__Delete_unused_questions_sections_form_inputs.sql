-- IFS-5874

-- Update questions that use the cost category type sections to use the 'Your project costs'.
update question q
inner join section current_section on q.section_id=current_section.id
set q.section_id = (select s.id from section s where s.competition_id=q.competition_id and s.section_type='PROJECT_COST_FINANCES')
where current_section.name in ('Labour',
'Overhead costs',
'Materials',
'Capital usage',
'Subcontracting costs',
'Travel and subsistence',
'Other costs');

-- delete the cost category type sections.
delete from section where name in  ('Labour',
'Overhead costs',
'Materials',
'Capital usage',
'Subcontracting costs',
'Travel and subsistence',
'Other costs');

-- Delete form inputs from the guidance questions
delete fi from form_input as fi
inner join question q on q.id = fi.question_id
inner join section s on s.id = q.section_id
where s.section_type='PROJECT_COST_FINANCES' and q.name is null;

-- Delete question statuses from the guidance questions
delete qs from question_status as qs
inner join question q on q.id = qs.question_id
inner join section s on s.id = q.section_id
where s.section_type='PROJECT_COST_FINANCES' and q.name is null;

-- Delete the guidance questions
delete q from question as q
inner join section s on s.id = q.section_id
where s.section_type='PROJECT_COST_FINANCES' and q.name is null;
