--
-- Remove the concept of child / parent question relationship now that a question holds one or more form inputs within it
--
alter table question drop FOREIGN KEY `FK_l4j1k8qe6sldwi2nfd1lguwhi`;
alter table question drop column child_question_id;