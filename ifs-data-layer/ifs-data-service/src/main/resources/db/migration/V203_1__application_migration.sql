-- IFS-9359: Application migration to avoid duplication.

ALTER TABLE application ADD COLUMN previous_application_id bigint(20) NULL;

-- IFS-9359: Application migration table to hold the applications that need migration.

CREATE TABLE application_migration (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  status enum('CREATED', 'MIGRATED') NOT NULL,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_on TIMESTAMP NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;