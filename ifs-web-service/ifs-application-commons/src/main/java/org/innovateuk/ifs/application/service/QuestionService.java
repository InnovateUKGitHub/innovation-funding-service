package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionService {
    ServiceResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    void markAsIncomplete(Long questionId, Long applicationId, Long markedAsInCompleteById);
    List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId);
    void removeNotifications(List<QuestionStatusResource> questionStatuses);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
    QuestionResource getByIdAndAssessmentId(Long questionId, Long assessmentId);
    Optional<QuestionResource> getNextQuestion(Long questionId);
    Optional<QuestionResource> getPreviousQuestion(Long questionId);
    Optional<QuestionResource> getPreviousQuestionBySection(Long sectionId);
    Optional<QuestionResource> getNextQuestionBySection(Long sectionId);
    ServiceResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);
    Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId);
    QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);
    Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
    List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId);
    void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy);
    Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request);
    void assignQuestion(Long applicationId, UserResource user, HttpServletRequest request);
}
