package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Question} related data.
 */
public interface QuestionRestService {
    RestResult<Void> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsInComplete(Long questionId, Long applicationId,  Long markedAsInCompleteById);
    RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    RestResult<List<Question>> findByCompetition(Long competitionId);
    RestResult<Void> updateNotification(Long questionStatusId, Boolean notify);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
    RestResult<Question> findById(Long questionId);
    RestResult<Question> getNextQuestion(Long questionId);
    RestResult<Question> getPreviousQuestion(Long questionId);
    RestResult<Question> getPreviousQuestionBySection(Long sectionId);
    RestResult<Question> getNextQuestionBySection(Long sectionId);
    RestResult<Question> getQuestionByFormInputType(String formInputType);
}
