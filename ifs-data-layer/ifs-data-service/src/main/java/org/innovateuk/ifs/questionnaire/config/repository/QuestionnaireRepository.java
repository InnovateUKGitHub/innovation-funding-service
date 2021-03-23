package org.innovateuk.ifs.questionnaire.config.repository;

import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.springframework.data.repository.CrudRepository;

public interface QuestionnaireRepository extends CrudRepository<Questionnaire, Long> {
}
