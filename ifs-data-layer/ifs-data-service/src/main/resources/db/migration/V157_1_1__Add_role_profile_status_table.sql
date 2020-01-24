-- IFS-7052 adding table to capture reason for making assessors unavailable.

CREATE TABLE role_profile_status (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  user_id bigint(20) NOT NULL UNIQUE,
  role_profile_state enum('ACTIVE','UNAVAILABLE','DISABLED'),
  role enum('ASSESSOR'),
  description VARCHAR(255),
    -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,
  KEY role_profile_status_created_by_to_user_fk (created_by),
  KEY role_profile_status_modified_by_to_user_fk (modified_by),
  CONSTRAINT role_profile_status_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT role_profile_status_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id),
  CONSTRAINT role_profile_status_user_id FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;