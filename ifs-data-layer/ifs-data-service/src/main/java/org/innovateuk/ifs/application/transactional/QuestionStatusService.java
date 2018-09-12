package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.Question;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * Transactional service for application question statuses
 */
public interface QuestionStatusService {
    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<List<ValidationMessages>> markAsComplete(final QuestionApplicationCompositeId ids,
                                                           final long markedAsCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<List<ValidationMessages>> markAsInComplete(final QuestionApplicationCompositeId ids,
                                                             final long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#ids, 'MARK_TEAM_INCOMPLETE')")
    ServiceResult<List<ValidationMessages>> markTeamAsInComplete(final QuestionApplicationCompositeId ids,
                                                                 final long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<Void> assign(final QuestionApplicationCompositeId ids,
                               final long assigneeId,
                               final long assignedById);


    @PreAuthorize("hasPermission(#applicationId, " +
            "'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getMarkedAsComplete(long applicationId,
                                                 long organisationId);

    @PreAuthorize("hasPermission(#questionStatusId, " +
            "'org.innovateuk.ifs.application.resource.QuestionStatusResource', 'UPDATE')")
    ServiceResult<Void> updateNotification(final long questionStatusId,
                                           final boolean notify);
    @PreAuthorize("hasPermission(#applicationId, " +
            "'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, long applicationId, long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(long questionId,
                                                                                              long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(
            long questionId, long applicationId, long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(
            Long[] questionIds, long applicationId, long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(long applicationId,
                                                                                 long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(long id);

    @PreAuthorize("hasPermission(#applicationId, " +
            "'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(long applicationId, long assigneeId);

}
