
DELETE fir FROM form_input_response AS fir
INNER JOIN form_input fi ON fi.id = fir.form_input_id
WHERE fi.form_input_type_id not in (2, 4, 21, 22, 23, 24, 25, 26, 27, 28, 29);

DELETE qs from question_status qs
inner join process_role pr on pr.id = marked_as_complete_by_id
inner join question q on q.id= qs.question_id
where q.multiple_statuses = 0
and pr.role_id != 1
and qs.marked_as_complete = 1;

DELETE qs from question_status qs
inner join question q on q.id= qs.question_id
where q.mark_as_completed_enabled = 0
and qs.marked_as_complete = 1;