CREATE TABLE application_procurement_milestone (

  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  application_finance_id BIGINT(20) NOT NULL,

  description varchar(255),
  month int(11),
  payment int(11),
  task_or_activity text,
  deliverable text,
  success_criteria text,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  KEY application_procurement_milestone_application_finance_fk (application_finance_id),
  CONSTRAINT application_procurement_milestone_application_finance_fk FOREIGN KEY (application_finance_id) REFERENCES application_finance(id),

  -- auditable constraints
  KEY application_procurement_milestone_created_by_to_user_fk (created_by),
  KEY application_procurement_milestone_modified_by_to_user_fk (modified_by),
  CONSTRAINT application_procurement_milestone_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT application_procurement_milestone_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
);


CREATE TABLE project_procurement_milestone (

  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  project_finance_id BIGINT(20) NOT NULL,
  application_procurement_milestone_id BIGINT(20),

  description varchar(255),
  month int(11),
  payment int(11),
  task_or_activity text,
  deliverable text,
  success_criteria text,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  KEY ppm_apm_fk (application_procurement_milestone_id),
  CONSTRAINT ppm_apm_fk FOREIGN KEY (application_procurement_milestone_id) REFERENCES application_procurement_milestone(id),
  KEY project_procurement_milestone_project_finance_fk (project_finance_id),
  CONSTRAINT project_procurement_milestone_project_finance_fk FOREIGN KEY (project_finance_id) REFERENCES project_finance(id),

  -- auditable constraints
  KEY project_procurement_milestone_created_by_to_user_fk (created_by),
  KEY project_procurement_milestone_modified_by_to_user_fk (modified_by),
  CONSTRAINT project_procurement_milestone_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT project_procurement_milestone_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id)
);