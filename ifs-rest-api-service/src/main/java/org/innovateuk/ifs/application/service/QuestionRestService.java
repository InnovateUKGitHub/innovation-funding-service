package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;

import java.util.List;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionRestService {
    RestResult<List<QuestionResource>> findByCompetition(long competitionId);

    RestResult<QuestionResource> findById(long questionId);

    RestResult<QuestionResource> getNextQuestion(long questionId);

    RestResult<QuestionResource> getPreviousQuestion(long questionId);

    RestResult<QuestionResource> getPreviousQuestionBySection(long sectionId);

    RestResult<QuestionResource> getNextQuestionBySection(long sectionId);

    RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(long competitionId, FormInputType formInputType);

    RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(long sectionId, QuestionType type);

    RestResult<QuestionResource> save(QuestionResource questionResource);

    RestResult<QuestionResource> getByIdAndAssessmentId(long questionId, long assessmentId);

    RestResult<List<QuestionResource>> getQuestionsByAssessment(long assessmentId);


}
