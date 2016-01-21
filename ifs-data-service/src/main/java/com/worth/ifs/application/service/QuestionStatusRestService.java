package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;

import java.util.List;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
  List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
  QuestionStatus findQuestionStatusById(final Long id);
}
