-- IFS-4458 - Project Documents - Add unique constraint to document_config table

ALTER TABLE document_config
ADD CONSTRAINT `UC_competition_title` UNIQUE (`competition_id`, `title`);