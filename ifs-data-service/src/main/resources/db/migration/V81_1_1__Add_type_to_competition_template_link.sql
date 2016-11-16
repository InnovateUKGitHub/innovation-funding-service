-- Add competition type link to a competition that is a template for this type.
ALTER TABLE `competition_type` ADD COLUMN `template_competition_id` bigint(20) DEFAULT NULL;
ALTER TABLE competition_type ADD CONSTRAINT template_competition_fk FOREIGN KEY (template_competition_id) REFERENCES competition(id);

ALTER TABLE `competition` ADD COLUMN `template` bit(1) DEFAULT 0