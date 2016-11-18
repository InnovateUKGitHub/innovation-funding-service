package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionResourceListType;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionResourceListType;
import static java.util.Arrays.asList;

/**
 * QuestionRestServiceImpl is a utility for CRUD operations on {@link QuestionResource}.
 * This class connects to the { com.worth.ifs.application.controller.QuestionController}
 * through a REST call.
 */
@Service
public class QuestionRestServiceImpl extends BaseRestService implements QuestionRestService {

    String questionRestURL = "/question";

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        return putWithRestResult(questionRestURL + "/markAsComplete/" + questionId + "/" + applicationId + "/" + markedAsCompleteById, new ParameterizedTypeReference<List<ValidationMessages>>() {
        });
    }

    @Override
    public RestResult<Void> markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        return putWithRestResult(questionRestURL + "/markAsInComplete/" + questionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        return putWithRestResult(questionRestURL + "/assign/" + questionId + "/" + applicationId + "/" + assigneeId + "/" + assignedById, Void.class);
    }

    @Override
    public RestResult<List<QuestionResource>> findByCompetition(Long competitionId) {
        return getWithRestResult(questionRestURL + "/findByCompetition/" + competitionId, ParameterizedTypeReferences.questionResourceListType());
    }

    @Override
    public RestResult<Void> updateNotification(Long questionStatusId, Boolean notify) {
        return putWithRestResult(questionRestURL + "/updateNotification/" + questionStatusId + "/" + notify, Void.class);
    }

    @Override
    public Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return adapt(restGetAsync(questionRestURL + "/getMarkedAsComplete/" + applicationId + "/" + organisationId, Long[].class), re -> new HashSet<>(asList(re.getBody())));
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
    public RestResult<QuestionResource> getQuestionByFormInputType(String formInputType) {
        return getWithRestResult(questionRestURL + "/getQuestionByFormInputType/" + formInputType, QuestionResource.class);
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
