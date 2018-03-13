package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
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
                                                           final Long markedAsCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<List<ValidationMessages>> markAsInComplete(final QuestionApplicationCompositeId ids,
                                                             final Long markedAsInCompleteById);

    @PreAuthorize("hasPermission(#ids, 'UPDATE')")
    ServiceResult<Void> assign(final QuestionApplicationCompositeId ids,
                               final Long assigneeId,
                               final Long assignedById);


    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<Long>> getMarkedAsComplete(Long applicationId,
                                                 Long organisationId);

    @PreAuthorize("hasPermission(#questionStatusId, 'org.innovateuk.ifs.application.resource.QuestionStatusResource', 'UPDATE')")
    ServiceResult<Void> updateNotification(final Long questionStatusId,
                                           final Boolean notify);
    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> isMarkedAsComplete(Question question, Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(Long questionId, Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(Long[] questionIds, Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<QuestionStatusResource> getQuestionStatusResourceById(Long id);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Integer> getCountByApplicationIdAndAssigneeId(Long applicationId, Long assigneeId);

}
