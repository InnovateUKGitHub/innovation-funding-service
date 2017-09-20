-- Remove indexes after they are no longer needed
alter table question drop index short_name;
alter table question drop index short_name_competition_id;
alter table question drop index name;
alter table form_input drop index description;
alter table user drop index email;
alter table section drop index name;