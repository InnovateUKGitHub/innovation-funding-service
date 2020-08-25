ALTER table knowledge_base
    ADD COLUMN organisation_type_id BIGINT(20),
    ADD CONSTRAINT fk_organisation_type_id FOREIGN KEY (organisation_type_id) REFERENCES organisation_type(id);

ALTER table knowledge_base
    ADD COLUMN registration_number VARCHAR(10);

ALTER table knowledge_base
    ADD COLUMN address_id BIGINT(20);
