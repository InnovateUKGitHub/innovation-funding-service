-- IFS-7370: Make assessor score available in notifications: Domain for storing assessor scores & emails

CREATE TABLE average_assessor_score (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  score double NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (application_id),
  CONSTRAINT application_id FOREIGN KEY (application_id) REFERENCES application (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;