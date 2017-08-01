package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionRestService {
    RestResult<List<ValidationMessages>> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById);

    RestResult<Void> markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById);

    RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById);

    RestResult<List<QuestionResource>> findByCompetition(Long competitionId);

    RestResult<Void> updateNotification(Long questionStatusId, Boolean notify);

    Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId);

    RestResult<QuestionResource> findById(Long questionId);

    RestResult<QuestionResource> getNextQuestion(Long questionId);

    RestResult<QuestionResource> getPreviousQuestion(Long questionId);

    RestResult<QuestionResource> getPreviousQuestionBySection(Long sectionId);

    RestResult<QuestionResource> getNextQuestionBySection(Long sectionId);

    RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType);

    RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(Long sectionId, QuestionType type);

    RestResult<QuestionResource> save(QuestionResource questionResource);

    RestResult<QuestionResource> getByIdAndAssessmentId(Long questionId, Long assessmentId);

    RestResult<List<QuestionResource>> getQuestionsByAssessment(Long assessmentId);


}
