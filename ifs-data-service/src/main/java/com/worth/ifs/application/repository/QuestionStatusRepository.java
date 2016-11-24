package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface QuestionStatusRepository extends CrudRepository<QuestionStatus, Long> {
    QuestionStatus findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(Long questionId, Long applicationId, Long markAsCompleteById);
    QuestionStatus findByQuestionIdAndApplicationIdAndAssigneeId(@Param("questionId") Long questionId, @Param("applicationId") Long applicationId, @Param("assigneeId") Long assigneeId);
    List<QuestionStatus> findByQuestionIdAndApplicationId(@Param("questionId") Long questionId, @Param("applicationId") Long applicationId);
    List<QuestionStatus> findByQuestionIdIsInAndApplicationId(@Param("questionIds") List<Long> questionIds, @Param("applicationId") Long applicationId);
    List<QuestionStatus> findByApplicationId(@Param("applicationId") Long applicationId);
    List<QuestionStatus> findByApplicationIdAndAssigneeId(@Param("applicationId") Long applicationId, @Param("assigneeId") Long assigneeId);
    List<QuestionStatus> findByApplicationIdAndAssigneeIdOrAssignedById(@Param("applicationId") Long applicationId, @Param("assigneeId") Long assigneeId, @Param("assignedById") Long assignedById);
    int countByApplicationIdAndAssigneeId(@Param("applicationId") Long applicationId, @Param("assigneeId") Long assigneeId);
    List<QuestionStatus> findByApplicationIdAndMarkedAsCompleteById(@Param("applicationId") Long applicationId, @Param("markedAsCompleteById") Long markedAsCompleteById);
}