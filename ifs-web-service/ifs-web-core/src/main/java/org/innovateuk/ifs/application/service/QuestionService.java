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
    @NotSecured("Not currently secured")
    ServiceResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);

    @NotSecured("Not currently secured")
    List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);

    @NotSecured("Not currently secured")
    void markAsIncomplete(Long questionId, Long applicationId, Long markedAsInCompleteById);

    @NotSecured("Not currently secured")
    List<QuestionResource> findByCompetition(Long competitionId);

    @NotSecured("Not currently secured")
    List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId);

    @NotSecured("Not currently secured")
    void removeNotifications(List<QuestionStatusResource> questionStatuses);

    @NotSecured("Not currently secured")
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);

    @NotSecured("Not currently secured")
    QuestionResource getById(Long questionId);

    @NotSecured("Not currently secured")
    QuestionResource getByIdAndAssessmentId(Long questionId, Long assessmentId);

    @NotSecured("Not currently secured")
    Optional<QuestionResource> getNextQuestion(Long questionId);

    @NotSecured("Not currently secured")
    Optional<QuestionResource> getPreviousQuestion(Long questionId);

    @NotSecured("Not currently secured")
    Optional<QuestionResource> getPreviousQuestionBySection(Long sectionId);

    @NotSecured("Not currently secured")
    Optional<QuestionResource> getNextQuestionBySection(Long sectionId);

    @NotSecured("Not currently secured")
    ServiceResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    @NotSecured("Not currently secured")
    Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId);

    @NotSecured("Not currently secured")
    QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);

    @NotSecured("Not currently secured")
    Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);

    @NotSecured("Not currently secured")
    List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId);

    @NotSecured("Not currently secured")
    List<QuestionResource> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);

    @NotSecured("Not currently secured")
    QuestionResource save(QuestionResource questionResource);

    @NotSecured("Not currently secured")
    List<QuestionResource> getQuestionsByAssessment(long assessmentId);

    @NotSecured("Not currently secured")
    void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy);

    @NotSecured("Not currently secured")
    Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request);

    @NotSecured("Not currently secured")
    void assignQuestion(Long applicationId, UserResource user, HttpServletRequest request);
}
