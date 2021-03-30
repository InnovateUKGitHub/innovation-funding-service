CREATE TABLE project_organisation_questionnaire_response (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  questionnaire_response_id BINARY(16) NOT NULL,
  project_id BIGINT(20) NOT NULL,
  organisation_id BIGINT(20) NOT NULL,

  KEY poqr_to_qr (questionnaire_response_id),
  CONSTRAINT poqr_to_qr_fk FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_response(id),
  KEY poqr_to_project (project_id),
  CONSTRAINT poqr_to_project_fk FOREIGN KEY (project_id) REFERENCES project(id),
  KEY poqr_to_organisation (organisation_id),
  CONSTRAINT poqr_to_organisation_fk FOREIGN KEY (organisation_id) REFERENCES organisation(id)
);