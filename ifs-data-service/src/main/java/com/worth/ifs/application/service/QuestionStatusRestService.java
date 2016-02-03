package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.resource.QuestionStatusResource;

import java.util.List;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
  List<QuestionStatus> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
  List<QuestionStatus> findByQuestionAndApplicationAndOrganisation(final Long questionId, final Long applicationId, final Long organisationId);
  List<QuestionStatusResource> findByApplicationAndOrganisation(final Long applicationId, final Long organisationId);
  QuestionStatus findQuestionStatusById(final Long id);
  List<QuestionStatusResource> getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);
  List<QuestionStatus> getByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
  List<QuestionStatus> getByIds(final List<Long> ids);
}
