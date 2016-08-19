-- rename invite status names
UPDATE invite SET status = 'SENT' WHERE status = 'SEND';
UPDATE invite SET status = 'OPENED' WHERE status = 'ACCEPTED';

-- lookup table for invite statuses
CREATE TABLE invite_status (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL UNIQUE KEY
) DEFAULT CHARSET=utf8;

INSERT INTO invite_status (id, name)
VALUES (1, "CREATED"), (2, "SENT"), (3, "OPENED");

-- fk from invite -> invite_status
ALTER TABLE invite ADD CONSTRAINT invite_to_invite_status_fk FOREIGN KEY (status) REFERENCES invite_status(name);
