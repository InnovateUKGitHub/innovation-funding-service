package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
    RestResult<List<QuestionStatusResource>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId);
    RestResult<List<QuestionStatusResource>> findByQuestionAndApplicationAndOrganisation(final Long questionId, final Long applicationId, final Long organisationId);
    RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(final Long applicationId, final Long organisationId);
    RestResult<QuestionStatusResource> findQuestionStatusById(final Long id);
    RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
    RestResult<List<ValidationMessages>> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById);
    RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    RestResult<Void> updateNotification(Long questionStatusId, Boolean notify);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
}
