--
-- Delete question_type as it is now essentially form_input_type
--
alter table question drop FOREIGN KEY `FK_sjwlpsl4l3y7jyh202b9p4rwj`;
alter table question drop column question_type_id;

drop table question_type;