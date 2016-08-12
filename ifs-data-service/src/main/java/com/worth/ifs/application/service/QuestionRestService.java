package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Question} related data.
 */
public interface QuestionRestService {
    RestResult<List<ValidationMessages>> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);
    RestResult<Void> markAsInComplete(Long questionId, Long applicationId,  Long markedAsInCompleteById);
    RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);
    RestResult<List<QuestionResource>> findByCompetition(Long competitionId);
    RestResult<Void> updateNotification(Long questionStatusId, Boolean notify);
    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);
    RestResult<QuestionResource> findById(Long questionId);
    RestResult<QuestionResource> getNextQuestion(Long questionId);
    RestResult<QuestionResource> getPreviousQuestion(Long questionId);
    RestResult<QuestionResource> getPreviousQuestionBySection(Long sectionId);
    RestResult<QuestionResource> getNextQuestionBySection(Long sectionId);
    RestResult<QuestionResource> getQuestionByFormInputType(String formInputType);
    RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);
    RestResult<QuestionResource> save(QuestionResource questionResource);
    RestResult<List<QuestionResource>> getQuestionsByAssessment(Long assessmentId);
}
