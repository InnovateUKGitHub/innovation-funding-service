INSERT question_status (question_id, application_id, marked_as_complete_by_id, marked_as_complete, marked_as_complete_on)

SELECT
q.id                as question_id,
app.id              as application_id,
pr.id               as marked_as_complete_by_id,
1                   as marked_as_complete,
now()               as marked_as_complete_on

FROM organisation o
inner join process_role pr
on pr.organisation_id = o.id
inner join application app
on app.id = pr.application_id
inner join process p
on p.target_id = app.id and p.process_type = 'ApplicationProcess'
inner join competition c
on c.id = app.competition
inner join section s
on s.competition_id = c.id
inner join question q
on q.section_id = s.id

where s.section_type = 'ORGANISATION_FINANCES' and
c.funding_type = 'GRANT' and
p.activity_state_id in (27,28) and
o.organisation_type_id = 2

and not exists (select inner_qs.id from question_status inner_qs inner join process_role inner_pr on inner_pr.id = inner_qs.marked_as_complete_by_id where inner_qs.question_id = q.id and inner_pr.application_id = app.id and inner_pr.organisation_id = o.id)

group by o.id, app.id
