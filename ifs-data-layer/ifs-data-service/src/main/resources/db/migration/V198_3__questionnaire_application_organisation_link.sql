CREATE TABLE application_organisation_questionnaire_response (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  questionnaire_response_id BIGINT(20) NOT NULL,
  application_id BIGINT(20) NOT NULL,
  organisation_id BIGINT(20) NOT NULL,

  KEY aoqr_to_qr (questionnaire_response_id),
  CONSTRAINT aoqr_to_qr_fk FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_response(id),
  KEY aoqr_to_application (application_id),
  CONSTRAINT aoqr_to_application_fk FOREIGN KEY (application_id) REFERENCES application(id),
  KEY aoqr_to_organisation (organisation_id),
  CONSTRAINT aoqr_to_organisation_fk FOREIGN KEY (organisation_id) REFERENCES organisation(id)
);