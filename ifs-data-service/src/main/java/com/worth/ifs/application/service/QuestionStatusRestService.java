package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
    RestResult<List<QuestionStatus>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
    RestResult<List<QuestionStatus>> findByQuestionAndApplicationAndOrganisation(final Long questionId, final Long applicationId, final Long organisationId);
    RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(final Long applicationId, final Long organisationId);
    RestResult<QuestionStatus> findQuestionStatusById(final Long id);
    RestResult<List<QuestionStatusResource>> getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);
    RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
    RestResult<List<QuestionStatus>> getByIds(final List<Long> ids);
}
