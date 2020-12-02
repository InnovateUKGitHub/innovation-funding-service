-- ifs-8314 removing flag no longer used and link to template on competition type no longer used.

alter table competition drop column template;

alter table competition_type drop foreign key template_competition_fk;
alter table competition_type drop column template_competition_id;