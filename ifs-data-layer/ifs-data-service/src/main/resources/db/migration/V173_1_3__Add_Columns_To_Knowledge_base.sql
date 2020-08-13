ALTER table knowledge_base
    ADD COLUMN organisation_type_id BIGINT(20),
    ADD CONSTRAINT fk_organisation_type_id FOREIGN KEY (organisation_type_id) REFERENCES organisation_type(id);

-- maybe add unique constraint
ALTER table knowledge_base
    ADD COLUMN identifier VARCHAR(10);