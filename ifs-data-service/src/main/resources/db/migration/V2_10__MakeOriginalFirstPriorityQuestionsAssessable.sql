--
-- Make the original Textarea questions with related Appendices assessable
--
update question q
join question_form_input qfi on q.id = qfi.question_id and qfi.priority = 0
set q.needing_assessor_score = true, q.needing_assessor_feedback = true
where exists (select 1 from question_form_input qfi2 where qfi2.priority = 1 and qfi2.question_id = q.id);