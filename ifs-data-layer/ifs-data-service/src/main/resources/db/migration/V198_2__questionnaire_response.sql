CREATE TABLE questionnaire_response (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  questionnaire_id BIGINT(20) NOT NULL,

  KEY questionnaire_response_to_questionnaire (questionnaire_id),
  CONSTRAINT questionnaire_response_to_questionnaire_fk FOREIGN KEY (questionnaire_id) REFERENCES questionnaire(id)
);

CREATE TABLE questionnaire_question_response (
  id BIGINT(20) PRIMARY KEY,
  questionnaire_response_id BIGINT(20) NOT NULL,
  questionnaire_option_id BIGINT(20) NOT NULL,

  KEY questionnaire_question_response_to_questionnaire_response (questionnaire_response_id),
  CONSTRAINT questionnaire_question_response_to_questionnaire_response_fk FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_response(id),
  KEY questionnaire_question_response_to_questionnaire_option (questionnaire_option_id),
  CONSTRAINT questionnaire_question_response_to_questionnaire_option_fk FOREIGN KEY (questionnaire_option_id) REFERENCES questionnaire_option(id)
);