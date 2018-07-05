package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * QuestionRestServiceImpl is a utility for CRUD operations on {@link QuestionResource}.
 * This class connects to the { org.innovateuk.ifs.application.controller.QuestionController}
 * through a REST call.
 */
@Service
public class QuestionRestServiceImpl extends BaseRestService implements QuestionRestService {

    String questionRestURL = "/question";

    @Override
    public RestResult<List<QuestionResource>> findByCompetition(long competitionId) {
        return getWithRestResult(questionRestURL + "/findByCompetition/" + competitionId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> findById(long questionId) {
        return getWithRestResult(questionRestURL + "/id/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestion(long questionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestion/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestion(long questionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestion/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestionBySection(long sectionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestionBySection/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestionBySection(long sectionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestionBySection/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(long competitionId, FormInputType formInputType) {
        return getWithRestResult(questionRestURL + "/getQuestionByCompetitionIdAndFormInputType/" + competitionId + "/" + formInputType.name(), QuestionResource.class);
    }

    @Override
    public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(
            long sectionId, QuestionType type) {
        return getWithRestResult(questionRestURL + "/getQuestionsBySectionIdAndType/" + sectionId + "/" + type.name(), ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> save(QuestionResource questionResource) {
        return putWithRestResult(questionRestURL + "/", questionResource, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getByIdAndAssessmentId(long questionId, long assessmentId) {
        return getWithRestResult(questionRestURL + "/getQuestionByIdAndAssessmentId/" + questionId + "/" + assessmentId, QuestionResource.class);
    }

    @Override
    public RestResult<List<QuestionResource>> getQuestionsByAssessment(long assessmentId) {
        return getWithRestResult(questionRestURL + "/getQuestionsByAssessment/" + assessmentId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndCompetitionSetupQuestionType(long questionId,
                                                                                                  CompetitionSetupQuestionType competitionSetupQuestionType) {
        return getWithRestResult(questionRestURL + "/getQuestionByCompetitionIdAndCompetitionSetupQuestionType/" +
                questionId + "/" + competitionSetupQuestionType.name(), QuestionResource.class);
    }
}
