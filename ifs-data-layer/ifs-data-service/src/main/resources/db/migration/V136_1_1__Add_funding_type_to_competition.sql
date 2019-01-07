ALTER TABLE competition ADD COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT');

-- triggers to sync public_content.funding_type -> competition.funding_type
CREATE TRIGGER insert_public_content_to_competition AFTER INSERT ON public_content
FOR EACH ROW
  UPDATE competition c SET c.funding_type = NEW.funding_type WHERE c.id = NEW.competition_id;

CREATE TRIGGER update_public_content_to_competition AFTER UPDATE ON public_content
FOR EACH ROW
  UPDATE competition c SET c.funding_type = NEW.funding_type WHERE c.id = NEW.competition_id;

UPDATE competition c SET funding_type = (SELECT funding_type FROM public_content pc WHERE pc.competition_id = c.id);

-- TODO IFS-4982 drop sync triggers and drop public_content.funding_type
-- DROP TRIGGER insert_public_content_to_competition;
-- DROP TRIGGER update_public_content_to_competition;
-- ALTER TABLE public_content DROP column funding_type;