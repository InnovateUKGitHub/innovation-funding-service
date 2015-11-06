--
-- Move Response input values into Form Input Responses
--
insert into form_input_response
select id, update_date, `value`, question_id, updated_by_id from response;