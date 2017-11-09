package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionService {
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void markAsIncomplete(Long questionId, Long applicationId, Long markedAsInCompleteById);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<QuestionResource> findByCompetition(Long competitionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void removeNotifications(List<QuestionStatusResource> questionStatuses);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    QuestionResource getById(Long questionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    QuestionResource getByIdAndAssessmentId(Long questionId, Long assessmentId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<QuestionResource> getNextQuestion(Long questionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<QuestionResource> getPreviousQuestion(Long questionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<QuestionResource> getPreviousQuestionBySection(Long sectionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<QuestionResource> getNextQuestionBySection(Long sectionId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<QuestionResource> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    QuestionResource save(QuestionResource questionResource);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<QuestionResource> getQuestionsByAssessment(long assessmentId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    void assignQuestion(Long applicationId, UserResource user, HttpServletRequest request);
}
