ALTER TABLE terms_and_conditions MODIFY COLUMN type enum('SITE','GRANT','GOL');

ALTER TABLE competition ADD COLUMN gol_template_id BIGINT(20) DEFAULT NULL;
ALTER TABLE competition ADD CONSTRAINT fk_competition_gol_template FOREIGN KEY(gol_template_id) REFERENCES terms_and_conditions(id);