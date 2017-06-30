ALTER TABLE spend_profile ADD COLUMN generated_date datetime NOT NULL;
ALTER TABLE spend_profile ADD COLUMN generated_by_id bigint(20) NOT NULL;

ALTER TABLE spend_profile ADD CONSTRAINT generated_by_fk FOREIGN KEY (generated_by_id) REFERENCES user(id);
