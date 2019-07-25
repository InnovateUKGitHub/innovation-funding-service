-- IFS-6191 per competition terms for sbri

ALTER table competition
    ADD COLUMN competition_terms_file_entry_id BIGINT(20),
    ADD CONSTRAINT fk_competition_terms_file_entry_id FOREIGN KEY (competition_terms_file_entry_id) REFERENCES file_entry(id);

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES (12, 'SBRI', 'sbri-terms-and-conditions', 1, 'GRANT', 15, now(), now(), 15);
