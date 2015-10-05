package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionStatusRepository extends CrudRepository<QuestionStatus, Long> {
    QuestionStatus findByQuestionIdAndApplicationIdAndMarkedAsCompleteById(Long questionId, Long applicationId, Long markAsCompleteById);
    QuestionStatus findByQuestionIdAndApplicationIdAndAssigneeId(Long questionId, Long applicationId, Long assigneeId);
    List<QuestionStatus> findByQuestionIdAndApplicationId(Long questionId, Long applicationId);
    List<QuestionStatus> findByApplicationId(Long applicationId);
}
