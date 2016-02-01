package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;

import java.util.List;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
  List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
  List<QuestionStatus> findByQuestionAndApplicationAndOrganisation(final Long questionId, final Long applicationId, final Long organisationId);
  List<QuestionStatus> findByApplicationAndOrganisation(final Long applicationId, final Long organisationId);
  QuestionStatus findQuestionStatusById(final Long id);
}
