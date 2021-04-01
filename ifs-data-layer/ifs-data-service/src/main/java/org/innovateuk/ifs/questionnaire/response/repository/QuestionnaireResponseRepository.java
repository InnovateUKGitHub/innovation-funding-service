package org.innovateuk.ifs.questionnaire.response.repository;

import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface QuestionnaireResponseRepository extends CrudRepository<QuestionnaireResponse, UUID> {
}
