-- IFS-7138 Adding table to capture deleted applications.

CREATE TABLE deleted_application_audit (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL UNIQUE,
  deleted_by BIGINT(20) NOT NULL,
  deleted_on DATETIME NOT NULL,
  KEY deleted_application_audit_deleted_by_to_user_fk (deleted_by),
  CONSTRAINT deleted_application_audit_deleted_by_to_user_fk FOREIGN KEY (deleted_by) REFERENCES user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;