package org.innovateuk.ifs.questionnaire.config.repository;

import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.springframework.data.repository.CrudRepository;

public interface QuestionnaireQuestionRepository extends CrudRepository<QuestionnaireQuestion, Long> {
}
