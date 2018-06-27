package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Rest service that exposes question status data
 */
public interface QuestionStatusRestService {
    RestResult<List<QuestionStatusResource>> findQuestionStatusesByQuestionAndApplicationId(final long questionId, final long applicationId);
    RestResult<List<QuestionStatusResource>> findByQuestionAndApplicationAndOrganisation(final long questionId, final long applicationId, final long organisationId);
    RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(final long applicationId, final long organisationId);
    RestResult<QuestionStatusResource> findQuestionStatusById(final long id);
    RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, long applicationId, long organisationId);
    RestResult<List<ValidationMessages>> markAsComplete(long questionId, long applicationId, long markedAsCompleteById);
    RestResult<Void> markAsInComplete(long questionId, long applicationId, long markedAsInCompleteById);
    RestResult<Void> markTeamAsInComplete(long questionId, long applicationId, long markedAsInCompleteById);
    RestResult<Void> assign(long questionId, long applicationId, long assigneeId, long assignedById);
    RestResult<Void> updateNotification(long questionStatusId, boolean notify);
    Future<Set<Long>> getMarkedAsComplete(long applicationId, long organisationId);
}
