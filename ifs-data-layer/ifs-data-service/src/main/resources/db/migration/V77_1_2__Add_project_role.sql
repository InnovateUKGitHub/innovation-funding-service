CREATE TABLE project_role (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL UNIQUE KEY
) DEFAULT CHARSET=utf8;


INSERT INTO project_role (id, name)
VALUES
  (1, 'PROJECT_PARTNER'),
  (2, 'PROJECT_MANAGER'),
  (3, 'PROJECT_FINANCE_CONTACT');

-- add invite column to competition_user
ALTER TABLE project_user ADD COLUMN project_role VARCHAR(255) NOT NULL;

-- migrate the old role data to the new column
UPDATE project_user SET project_role = 'PROJECT_PARTNER' WHERE role_id = 10;
UPDATE project_user SET project_role = 'PROJECT_MANAGER' WHERE role_id = 11;
UPDATE project_user SET project_role = 'PROJECT_FINANCE_CONTACT' WHERE role_id = 9;

-- add the fk constraint to project_user.project_role
ALTER TABLE project_user ADD CONSTRAINT project_user_to_projet_role_fk FOREIGN KEY (project_role) REFERENCES project_role(name);

-- drop the old role column
ALTER TABLE project_user
  DROP FOREIGN KEY project_user_to_role_fk,
  DROP COLUMN role_id;


