--
-- Add column to add additional meaning to the individual form inputs that make up a Question
--
alter table form_input add column `description` varchar(255);

update form_input fi
join question_form_input qfi on qfi.form_input_id = fi.id and qfi.priority = 0
join question q on q.id = qfi.question_id
set fi.`description` = q.name;

update form_input fi
join question_form_input qfi on qfi.form_input_id = fi.id and qfi.priority = 1
set `description` = 'Appendix';