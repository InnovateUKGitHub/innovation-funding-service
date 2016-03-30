package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.rest.RestResult;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Question} related data.
 */
public interface QuestionService {
    void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    void markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById);
    List<Question> findByCompetition(Long competitionId);
    List<QuestionStatusResource> getNotificationsForUser(Collection<QuestionStatusResource> questionStatuses, Long userId);
    void removeNotifications(List<QuestionStatusResource> questionStatuses);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
    Question getById(Long questionId);
    Optional<Question> getNextQuestion(Long questionId);
    Optional<Question> getPreviousQuestion(Long questionId);
    Optional<Question> getPreviousQuestionBySection(Long sectionId);
    Optional<Question> getNextQuestionBySection(Long sectionId);
    RestResult<Question> getQuestionByFormInputType(String formInputType);
    Map<Long, QuestionStatusResource> getQuestionStatusesForApplicationAndOrganisation(Long applicationId, Long userOrganisationId);
    QuestionStatusResource getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId);
    Map<Long, QuestionStatusResource> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId);
}
