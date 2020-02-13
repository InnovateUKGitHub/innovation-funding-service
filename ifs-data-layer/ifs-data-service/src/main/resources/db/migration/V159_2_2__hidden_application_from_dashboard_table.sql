-- IFS-7138 Adding table to hide application from users dashboard

CREATE TABLE hidden_application_from_dashboard (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  process_role_id bigint(20) NOT NULL UNIQUE,
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  KEY hidden_application_from_dashboard_created_by_to_user_fk (created_by),
  KEY hidden_application_from_dashboard_process_role_fk (process_role_id),
  CONSTRAINT hidden_application_from_dashboard_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT hidden_application_from_dashboard_process_role_fk FOREIGN KEY (process_role_id) REFERENCES process_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;