--Change the column name from application to application_id
ALTER TABLE project
CHANGE COLUMN `application` `application_id` BIGINT(20) NOT NULL;