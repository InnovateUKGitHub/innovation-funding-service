package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
  ListenableFuture<ResponseEntity<QuestionStatus[]>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
  QuestionStatus findQuestionStatusById(final Long id);
}
