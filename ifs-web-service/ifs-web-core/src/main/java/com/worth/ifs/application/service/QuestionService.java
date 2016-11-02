package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.user.resource.ProcessRoleResource;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionService {
    void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    List<ValidationMessages> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById);
    List<QuestionResource> findByCompetition(Long competitionId);
    List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId);
    void removeNotifications(List<QuestionStatusResource> questionStatuses);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
    QuestionResource getById(Long questionId);
    Optional<QuestionResource> getNextQuestion(Long questionId);
    Optional<QuestionResource> getPreviousQuestion(Long questionId);
    Optional<QuestionResource> getPreviousQuestionBySection(Long sectionId);
    Optional<QuestionResource> getNextQuestionBySection(Long sectionId);
    RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, String formInputType);
    Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId);
    QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);
    Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
    List<QuestionStatusResource> findQuestionStatusesByQuestionAndApplicationId(Long questionId, Long applicationId);
    List<QuestionResource> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);
    QuestionResource save(QuestionResource questionResource);
    List<QuestionResource> getQuestionsByAssessment(Long assessmentId);
    void assignQuestion(Long applicationId, HttpServletRequest request, ProcessRoleResource assignedBy);
    Long extractQuestionProcessRoleIdFromAssignSubmit(HttpServletRequest request);
}
