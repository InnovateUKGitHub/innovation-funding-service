ALTER TABLE invite
  ADD COLUMN innovation_category_id BIGINT(20),
  ADD CONSTRAINT invite_to_category_fk FOREIGN KEY (innovation_category_id) REFERENCES category(id);