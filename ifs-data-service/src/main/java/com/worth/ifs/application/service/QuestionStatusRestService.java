package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
    RestResult<List<QuestionStatusResource>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
    RestResult<List<QuestionStatusResource>> findByQuestionAndApplicationAndOrganisation(final Long questionId, final Long applicationId, final Long organisationId);
    RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(final Long applicationId, final Long organisationId);
    RestResult<QuestionStatusResource> findQuestionStatusById(final Long id);
    RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
}
