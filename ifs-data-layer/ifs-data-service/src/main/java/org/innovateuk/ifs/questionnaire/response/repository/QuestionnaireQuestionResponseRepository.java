package org.innovateuk.ifs.questionnaire.response.repository;

import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireQuestionResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireQuestionResponseRepository extends CrudRepository<QuestionnaireQuestionResponse, Long> {

    Optional<QuestionnaireQuestionResponse> findByOptionQuestionIdAndQuestionnaireResponseId(long questionId, UUID responseId);

    void deleteByQuestionnaireResponseIdAndOptionQuestionDepthGreaterThanEqualAndIdNot(UUID questionnaireResponseId, int depth, long id);
}
