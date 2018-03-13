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
    RestResult<List<QuestionResource>> findByCompetition(Long competitionId);

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
