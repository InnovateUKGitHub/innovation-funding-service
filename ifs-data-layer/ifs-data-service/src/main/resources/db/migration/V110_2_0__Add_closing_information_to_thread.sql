ALTER TABLE thread ADD COLUMN closed_by_user_id BIGINT(20) NULL;

ALTER TABLE thread ADD CONSTRAINT closed_by_user_id_fk FOREIGN KEY (closed_by_user_id)
  REFERENCES `user` (id);

ALTER TABLE thread ADD COLUMN closed_date TIMESTAMP NULL;