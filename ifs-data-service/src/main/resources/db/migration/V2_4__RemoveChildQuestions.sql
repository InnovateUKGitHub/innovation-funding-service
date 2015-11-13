--
-- Delete questions that are no longer required
--
delete from assessor_feedback where response_id in (select id from response where question_id in (select c.id from question p, question c where c.id = p.child_question_id));
delete from response where question_id in (select c.id from question p, question c where c.id = p.child_question_id);
update question set child_question_id = null;
delete from question where id not in (select question_id from question_form_input);