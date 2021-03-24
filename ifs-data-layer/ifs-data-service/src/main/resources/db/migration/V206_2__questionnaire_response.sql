CREATE TABLE questionnaire_response (
  id BINARY(16) NOT NULL PRIMARY KEY,
  questionnaire_id BIGINT(20) NOT NULL,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  -- auditable constraints
  KEY questionnaire_response_created_by_to_user_fk (created_by),
  KEY questionnaire_response_modified_by_to_user_fk (modified_by),
  CONSTRAINT questionnaire_response_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT questionnaire_response_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id),

  KEY questionnaire_response_to_questionnaire (questionnaire_id),
  CONSTRAINT questionnaire_response_to_questionnaire_fk FOREIGN KEY (questionnaire_id) REFERENCES questionnaire(id)
);

CREATE TABLE questionnaire_question_response (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  questionnaire_response_id BINARY(16) NOT NULL,
  questionnaire_option_id BIGINT(20) NOT NULL,

  KEY questionnaire_question_response_to_questionnaire_response (questionnaire_response_id),
  CONSTRAINT questionnaire_question_response_to_questionnaire_response_fk FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_response(id),
  KEY questionnaire_question_response_to_questionnaire_option (questionnaire_option_id),
  CONSTRAINT questionnaire_question_response_to_questionnaire_option_fk FOREIGN KEY (questionnaire_option_id) REFERENCES questionnaire_option(id)
);