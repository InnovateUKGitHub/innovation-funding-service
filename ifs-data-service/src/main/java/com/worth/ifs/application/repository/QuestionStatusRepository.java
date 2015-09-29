package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionStatusRepository extends CrudRepository<QuestionStatus, Long> {
    QuestionStatus findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(@Param("questionId") Long questionId, @Param("applicationId") Long applicationId, @Param("markedAsCompleteById") Long markAsCompleteById);
    QuestionStatus findByQuestionIdAndApplicationIdAndAssigneeId(@Param("questionId") Long questionId, @Param("applicationId") Long applicationId, @Param("assigneeId") Long assigneeId);
    List<QuestionStatus> findByQuestionIdAndApplicationId(@Param("questionId") Long questionId, @Param("applicationId") Long applicationId);

}
