package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface QuestionStatusRepository extends CrudRepository<QuestionStatus, Long> {
    QuestionStatus findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(Long questionId, Long applicationId, Long markAsCompleteById);
    QuestionStatus findByQuestionIdAndApplicationIdAndAssigneeId(Long questionId, Long applicationId, Long assigneeId);
    List<QuestionStatus> findByQuestionIdAndApplicationId(Long questionId, Long applicationId);
    List<QuestionStatus> findByQuestionIdAndApplicationIdAndMarkedAsComplete(Long questionId, Long applicationId, boolean markedAsComplete);
    List<QuestionStatus> findByQuestionIdIsInAndApplicationId(List<Long> questionIds, Long applicationId);
    List<QuestionStatus> findByApplicationId(long applicationId);
    List<QuestionStatus> findByApplicationIdAndAssigneeIdOrAssignedById(Long applicationId, Long assigneeId, Long assignedById);
    int countByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId);
    List<QuestionStatus> findByApplicationIdAndMarkedAsCompleteById(Long applicationId, Long markedAsCompleteById);
    List<QuestionStatus> findByQuestionIdAndApplicationIdAndMarkedAsCompleteAndMarkedAsCompleteByOrganisationId(Long questionId, Long applicationId, boolean markedAsComplete, Long organisationId);
    List<QuestionStatus> findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(long applicationId,
                                                                                              long markedAsCompleteById,
                                                                                              long assigneeId,
                                                                                              long assignedById);
    long countByApplicationIdAndMarkedAsCompleteTrueAndQuestionQuestionSetupTypeNotIn(long applicationId, Set<QuestionSetupType> questionSetupTypes);
    void deleteByApplicationId(long applicationId);
}
