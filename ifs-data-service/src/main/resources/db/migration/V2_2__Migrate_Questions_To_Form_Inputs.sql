--
-- Move Question Types into Form Input Types instead
--
insert into form_input_type
select id, title from question_type;

--
-- Move top-level Questions into Form Inputs
--
insert into form_input
select q.id, q.option_values, q.word_count, q.question_type_id from question q
where not exists (select 1 from question p where q.id = p.child_question_id);

--
-- Map top-level questions to their first Form Inputs
--
insert into question_form_input
select q.id, f.id, 0 from question q
join form_input f using(id);

--
-- Move 2nd-level Questions into Form Inputs
--
insert into form_input
select q.id, q.option_values, q.word_count, q.question_type_id from question q
where exists (select 1 from question p where q.id = p.child_question_id);

--
-- Map "child questions" to be the 2nd Form Input for their "parent" Questions
--
insert into question_form_input
select p.id, f.id, 1 from question p
join question c on c.id = p.child_question_id
join form_input f on f.id = c.id;