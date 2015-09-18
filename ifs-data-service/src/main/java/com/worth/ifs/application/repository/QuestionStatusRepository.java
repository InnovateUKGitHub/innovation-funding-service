package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionStatusRepository extends CrudRepository<QuestionStatus, Long> {
    QuestionStatus findByQuestionIdAndMarkedAsCompleteById(@Param("questionId") Long questionId, @Param("markedAsCompleteById") Long markAsCompleteById);
    QuestionStatus findByQuestionIdAndAssigneeId(@Param("questionId") Long questionId, @Param("assigneeId") Long assigneeId);
    List<QuestionStatus> findByQuestionId(@Param("questionId") Long questionId);

}
