alter table section_template add column display_in_assessment_overview TINYINT(1) NOT NULL DEFAULT 0;

update section_template set display_in_assessment_overview = 1 where name in ('Project details', 'Application questions', 'Finances', 'Finances overview');