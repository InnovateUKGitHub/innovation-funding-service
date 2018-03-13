package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
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
    public RestResult<List<QuestionResource>> findByCompetition(Long competitionId) {
        return getWithRestResult(questionRestURL + "/findByCompetition/" + competitionId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<QuestionResource> findById(Long questionId) {
        return getWithRestResult(questionRestURL + "/id/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestion(Long questionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestion/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestion(Long questionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestion/" + questionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getPreviousQuestionBySection(Long sectionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestionBySection/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getNextQuestionBySection(Long sectionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestionBySection/" + sectionId, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(Long competitionId, FormInputType formInputType) {
        return getWithRestResult(questionRestURL + "/getQuestionByCompetitionIdAndFormInputType/" + competitionId + "/" + formInputType.name(), QuestionResource.class);
    }

	@Override
	public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(
			Long sectionId, QuestionType type) {
		 return getWithRestResult(questionRestURL + "/getQuestionsBySectionIdAndType/" + sectionId + "/" + type.name(), ParameterizedTypeReferences.questionResourceListType());
	}

    @Override
    public RestResult<QuestionResource> save(QuestionResource questionResource) {
        return putWithRestResult(questionRestURL + "/", questionResource, QuestionResource.class);
    }

    @Override
    public RestResult<QuestionResource> getByIdAndAssessmentId(Long questionId, Long assessmentId) {
        return getWithRestResult(questionRestURL + "/getQuestionByIdAndAssessmentId/" + questionId + "/" + assessmentId, QuestionResource.class);
    }

    @Override
    public RestResult<List<QuestionResource>> getQuestionsByAssessment(Long assessmentId) {
        return getWithRestResult(questionRestURL + "/getQuestionsByAssessment/" + assessmentId, ParameterizedTypeReferences.questionResourceListType());
    }
}
