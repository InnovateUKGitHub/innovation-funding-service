ALTER TABLE question ADD questionnaire_id BIGINT(20) NULL;
ALTER TABLE question ADD CONSTRAINT question_questionnaire_fk FOREIGN KEY (questionnaire_id) REFERENCES questionnaire(id);