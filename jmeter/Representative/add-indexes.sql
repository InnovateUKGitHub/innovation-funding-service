-- Add some indexes and shortern some varchar columns to make the query execute faster.
-- Execute the query in a local (non docker) instance otherwise it will take a very long time to return
alter table question modify short_name varchar(63);
alter table question add index short_name (short_name);
alter table question add index short_name_competition_id (short_name, competition_id);
alter table question modify name varchar(63);
alter table question add index name (name);
alter table form_input modify description varchar(63);
alter table form_input add index description (description);
alter table user add index email (email);
alter table section add index name (name);