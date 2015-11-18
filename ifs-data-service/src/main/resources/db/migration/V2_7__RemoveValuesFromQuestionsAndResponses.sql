--
-- Remove the concept of child / parent question relationship now that a question holds one or more form inputs within it
--
alter table response drop column `value`;
alter table question drop column `word_count`;
alter table question drop column `option_values`;
alter table form_input drop column `option_values`;