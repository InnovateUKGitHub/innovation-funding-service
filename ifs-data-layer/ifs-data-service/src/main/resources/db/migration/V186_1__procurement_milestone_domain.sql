CREATE TABLE application_procurement_milestone (

  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  application_finance_id BIGINT(20) NOT NULL,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  description varchar(255),
  month int(11),
  payment int(11),
  task_or_activity text,
  deliverable text,
  success_criteria text,

  KEY application_procurement_milestone_application_finance_fk (application_finance_id),
  CONSTRAINT application_procurement_milestone_application_finance_fk FOREIGN KEY (application_finance_id) REFERENCES application_finance(id),

  KEY application_procurement_milestone_created_by_to_user_fk (created_by),
  KEY application_procurement_milestone_modified_by_to_user_fk (modified_by),
  CONSTRAINT application_procurement_milestone_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT application_procurement_milestone_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
);


CREATE TABLE project_procurement_milestone (

  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  project_finance_id BIGINT(20) NOT NULL,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  description varchar(255),
  month int(11),
  payment int(11),
  task_or_activity text,
  deliverable text,
  success_criteria text,

  KEY project_procurement_milestone_project_finance_fk (project_finance_id),
  CONSTRAINT project_procurement_milestone_project_finance_fk FOREIGN KEY (project_finance_id) REFERENCES project_finance(id),

  KEY project_procurement_milestone_created_by_to_user_fk (created_by),
  KEY project_procurement_milestone_modified_by_to_user_fk (modified_by),
  CONSTRAINT project_procurement_milestone_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT project_procurement_milestone_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
);