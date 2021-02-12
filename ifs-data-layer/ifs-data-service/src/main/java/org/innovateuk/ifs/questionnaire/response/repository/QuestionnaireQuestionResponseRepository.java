package org.innovateuk.ifs.questionnaire.response.repository;

import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireQuestionResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface QuestionnaireQuestionResponseRepository extends CrudRepository<QuestionnaireQuestionResponse, Long> {

    Optional<QuestionnaireQuestionResponse> findByOptionQuestionIdAndQuestionnaireResponseId(long questionId, long responseId);

    void deleteByQuestionnaireResponseIdAndOptionQuestionDepthGreaterThanEqualAndIdNot(long questionnaireResponseId, int depth, long id);
}
