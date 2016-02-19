package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.worth.ifs.application.service.Futures.adapt;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionListType;
import static java.util.Arrays.asList;

/**
 * QuestionRestServiceImpl is a utility for CRUD operations on {@link Question}.
 * This class connects to the {@link com.worth.ifs.application.controller.QuestionController}
 * through a REST call.
 */
@Service
public class QuestionRestServiceImpl extends BaseRestService implements QuestionRestService {
    @Value("${ifs.data.service.rest.question}")
    String questionRestURL;

    @Override
    public RestResult<Void> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        return putWithRestResult(questionRestURL + "/markAsComplete/" + questionId + "/" + applicationId + "/" + markedAsCompleteById, Void.class);
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
    public RestResult<List<Question>> findByCompetition(Long competitionId) {
        return getWithRestResult(questionRestURL + "/findByCompetition/" + competitionId, questionListType());
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
    public RestResult<Question> findById(Long questionId) {
        return getWithRestResult(questionRestURL + "/id/" + questionId, Question.class);
    }

    @Override
    public RestResult<Question> getNextQuestion(Long questionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestion/" + questionId, Question.class);
    }

    @Override
    public RestResult<Question> getPreviousQuestion(Long questionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestion/" + questionId, Question.class);
    }

    @Override
    public RestResult<Question> getPreviousQuestionBySection(Long sectionId) {
        return getWithRestResult(questionRestURL + "/getPreviousQuestionBySection/" + sectionId, Question.class);
    }

    @Override
    public RestResult<Question> getNextQuestionBySection(Long sectionId) {
        return getWithRestResult(questionRestURL + "/getNextQuestionBySection/" + sectionId, Question.class);
    }
}
