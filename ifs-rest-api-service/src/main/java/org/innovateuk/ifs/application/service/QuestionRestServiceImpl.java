package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
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
        return getWithRestResult(questionRestURL + "/find-by-competition/" + competitionId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> findById(long questionId) {
        return getWithRestResult(questionRestURL + "/id/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestion(long questionId) {
        return getWithRestResult(questionRestURL + "/get-next-question/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestion(long questionId) {
        return getWithRestResult(questionRestURL + "/get-previous-question/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestionBySection(long sectionId) {
        return getWithRestResult(questionRestURL + "/get-previous-question-by-section/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestionBySection(long sectionId) {
        return getWithRestResult(questionRestURL + "/get-next-question-by-section/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(long competitionId, FormInputType formInputType) {
        return getWithRestResult(questionRestURL + "/get-question-by-competition-id-and-form-input-type/" + competitionId + "/" + formInputType.name(), QuestionResource.class);
    }

    @Override
    public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(
            long sectionId, QuestionType type) {
        return getWithRestResult(questionRestURL + "/get-questions-by-section-id-and-type/" + sectionId + "/" + type.name(), ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> save(QuestionResource questionResource) {
        return putWithRestResult(questionRestURL + "/", questionResource, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getByIdAndAssessmentId(long questionId, long assessmentId) {
        return getWithRestResult(questionRestURL + "/get-question-by-id-and-assessment-id/" + questionId + "/" + assessmentId, QuestionResource.class);
    }

    @Override
    public RestResult<List<QuestionResource>> getQuestionsByAssessment(long assessmentId) {
        return getWithRestResult(questionRestURL + "/get-questions-by-assessment/" + assessmentId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndQuestionSetupType(long questionId,
                                                                                       QuestionSetupType questionSetupType) {
        return getWithRestResult(questionRestURL + "/get-question-by-competition-id-and-question-setup-type/" +
                questionId + "/" + questionSetupType.name(), QuestionResource.class);
    }
}
