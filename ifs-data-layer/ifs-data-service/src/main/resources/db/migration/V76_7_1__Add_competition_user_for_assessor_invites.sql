CREATE TABLE competition_user_status (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  name varchar(16) NOT NULL UNIQUE
);

INSERT INTO competition_user_status (id, name) VALUES
  (1, 'PENDING'),
  (2, 'ACCEPTED'),
  (3, 'REJECTED');


CREATE TABLE competition_role (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL
);

INSERT INTO competition_role (id, name) VALUES
  (1, 'ASSESSOR');


CREATE TABLE competition_user_rejection_reason (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  reason varchar(255) NOT NULL
);


CREATE TABLE competition_user (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  competition_role_id bigint(20),
  competition_id bigint(20),
  user_id bigint(20),
  invite_id bigint(20),
  competition_user_status_name varchar(16),
  rejection_reason_id bigint(20),
  rejection_comment longtext,

  CONSTRAINT competition_user_to_competition_role_fk FOREIGN KEY (competition_role_id) REFERENCES competition_role(id),
  CONSTRAINT competition_user_to_competition_fk FOREIGN KEY (competition_id) REFERENCES competition(id),
  CONSTRAINT competition_user_to_user_fk FOREIGN KEY (user_id) REFERENCES user(id),
  CONSTRAINT competition_user_to_competition_user_status_fk FOREIGN KEY (competition_user_status_name) REFERENCES competition_user_status(name),
  CONSTRAINT competition_user_to_competition_user_rejection_reason FOREIGN KEY (rejection_reason_id) REFERENCES competition_user_rejection_reason(id)
);

ALTER TABLE invite ADD UNIQUE KEY uk_hash (hash);
