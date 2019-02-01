ALTER TABLE competition ADD COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT');

UPDATE competition c SET funding_type = (SELECT funding_type FROM public_content pc WHERE pc.competition_id = c.id);

ALTER TABLE public_content DROP column funding_type;