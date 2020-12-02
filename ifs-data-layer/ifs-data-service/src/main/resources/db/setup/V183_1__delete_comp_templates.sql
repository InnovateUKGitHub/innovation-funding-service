-- ifs-8314 deleting templates from db, as we now don't use the db for templates
delete from competition where template = 1;