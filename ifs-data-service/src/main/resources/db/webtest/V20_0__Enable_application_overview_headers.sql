update question set name = 'Application details', short_name = 'Application details' where name = 'Project summary' and short_name = 'Summary';

update form_input set description = 'Application details' where description = 'First Question';

update form_input set form_input_type_id = 5 where description = 'Application details';

update application set start_date = '2020-10-01 00:00:00' where start_date is null;