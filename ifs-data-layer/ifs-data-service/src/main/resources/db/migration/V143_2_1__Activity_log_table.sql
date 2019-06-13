-- IFS-5820

CREATE TABLE activity_log (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  type enum('APPLICATION_SUBMITTED') NOT NULL,
  created_on DATETIME NOT NULL,
  created_by bigint(20) NOT NULL,
  thread_id bigint(20),
  CONSTRAINT fk_activity_log_application_id FOREIGN KEY (application_id) REFERENCES application(id),
  CONSTRAINT fk_activity_log_user_id FOREIGN KEY (created_by) REFERENCES user(id),
  CONSTRAINT fk_activity_log_thread_id FOREIGN KEY (thread_id) REFERENCES thread(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
