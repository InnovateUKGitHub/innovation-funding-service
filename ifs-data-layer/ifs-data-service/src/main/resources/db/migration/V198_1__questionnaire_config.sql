CREATE TABLE questionnaire (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT
);

ALTER TABLE question ADD questionnaire_id BIGINT(20) NULL;

ALTER TABLE question ADD CONSTRAINT question_questionnaire_fk FOREIGN KEY (questionnaire_id) REFERENCES questionnaire(id);

CREATE TABLE questionnaire_decision (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE questionnaire_question (
  id BIGINT(20) PRIMARY KEY,
  questionnaire_id BIGINT(20) NOT NULL,

  priority int(11) NOT NULL,
  title VARCHAR(255) NOT NULL,
  question VARCHAR(255) NOT NULL,
  guidance VARCHAR(255) NULL,

  KEY questionnaire_question_to_questionnaire (questionnaire_id),
  CONSTRAINT questionnaire_question_to_questionnaire_fk FOREIGN KEY (questionnaire_id) REFERENCES questionnaire(id),
  KEY questionnaire_question_to_questionnaire_decision (id),
  CONSTRAINT questionnaire_question_to_questionnaire_decision_fk FOREIGN KEY (id) REFERENCES questionnaire_decision(id)
);

CREATE TABLE questionnaire_text_outcome (
  id BIGINT(20) PRIMARY KEY,
  text VARCHAR(255) NOT NULL,

  KEY questionnaire_text_outcome_to_questionnaire_decision (id),
  CONSTRAINT questionnaire_text_outcome_to_questionnaire_decision_fk FOREIGN KEY (id) REFERENCES questionnaire_decision(id)
);

CREATE TABLE questionnaire_option (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  questionnaire_question_id BIGINT(20) NOT NULL,
  questionnaire_decision_id BIGINT(20) NOT NULL,
  text VARCHAR(255) NOT NULL,

  KEY questionnaire_option_to_questionnaire_question (questionnaire_question_id),
  CONSTRAINT questionnaire_option_to_questionnaire_question_fk FOREIGN KEY (questionnaire_question_id) REFERENCES questionnaire_question(id),
  KEY questionnaire_option_to_questionnaire_decision (questionnaire_decision_id),
  CONSTRAINT questionnaire_option_to_questionnaire_decision_fk FOREIGN KEY (questionnaire_decision_id) REFERENCES questionnaire_decision(id)
);