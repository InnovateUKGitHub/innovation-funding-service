-- IFS-7138 Adding table to hide application from users dashboard

CREATE TABLE application_hidden_from_dashboard (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  user_id bigint(20) NOT NULL,
  created_on DATETIME NOT NULL,
  KEY application_hidden_from_dashboard_user_fk (user_id),
  KEY application_hidden_from_dashboard_application_fk (application_id),
  UNIQUE KEY application_hidden_user_id_application (application_id, user_id),
  CONSTRAINT application_hidden_from_dashboard_user_fk FOREIGN KEY (user_id) REFERENCES user (id),
  CONSTRAINT application_hidden_from_dashboard_application_fk FOREIGN KEY (application_id) REFERENCES application (id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;