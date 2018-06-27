CREATE TABLE process_history (

  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  process_id BIGINT(20) NOT NULL,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  KEY process_id_to_process_fk (process_id),
  CONSTRAINT process_history_process_id_to_process_fk FOREIGN KEY (process_id) REFERENCES process(id),

  KEY process_history_created_by_to_user_fk (created_by),
  KEY process_history_modified_by_to_user_fk (modified_by),
  CONSTRAINT process_history_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT process_history_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
);