-- IFS-7052 adding table to capture reason for making assessors unavailable.

CREATE TABLE user_rejection_status (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  user_id bigint(20) NOT NULL UNIQUE,
  status enum('UNAVAILABLE','DISABLED'),
  rejection_reason VARCHAR(255),
  rejected_by bigint(20) NOT NULL,
  rejected_on datetime,

    -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  KEY user_rejection_status_created_by_to_user_fk (created_by),
  KEY user_rejection_status_modified_by_to_user_fk (modified_by),
  CONSTRAINT user_rejection_status_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT user_rejection_status_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
  CONSTRAINT user_rejection_status_user_id FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;