-- IFS-4520 remove all questions with no name in the same section as a cost

create temporary table questions_to_delete (
  id bigint(20)
);

insert into questions_to_delete
select id
from question
where question_type = 'GENERAL'
  and name is NULL
  and section_id in
      (select section_id
       from (SELECT * FROM ifs.question) as alias
       where question_type = 'COST'
          or name = 'Overheads');

delete
from form_input
where question_id in (select id from questions_to_delete);

delete
from question_status
where question_id in (select id from questions_to_delete);

delete
from question
where id in (select id from questions_to_delete);

drop table questions_to_delete;
