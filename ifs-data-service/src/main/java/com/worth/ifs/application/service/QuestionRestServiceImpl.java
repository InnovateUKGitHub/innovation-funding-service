package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * QuestionRestServiceImpl is a utility for CRUD operations on {@link Question}.
 * This class connects to the {@link com.worth.ifs.application.controller.QuestionController}
 * through a REST call.
 */
@Service
public class QuestionRestServiceImpl extends BaseRestService implements  QuestionRestService {
    @Value("${ifs.data.service.rest.question}")
    String questionRestURL;

    @Override
    public void markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        restPut(questionRestURL + "/markAsComplete/"+questionId + "/" + applicationId + "/" + markedAsCompleteById);
    }

    @Override
    public void markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        restPut(questionRestURL + "/markAsInComplete/" + questionId + "/" + applicationId + "/" + markedAsInCompleteById);
    }

    @Override
    public void assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        restPut(questionRestURL + "/assign/" + questionId + "/" + applicationId + "/" + assigneeId + "/" + assignedById);
    }

    @Override
    public List<Question> findByCompetition(Long competitionId) {
        return Arrays.asList(restGet(questionRestURL + "/findByCompetition/" + competitionId, Question[].class));
    }

    @Override
    public void updateNotification(Long questionStatusId, Boolean notify) {
        restPut(questionRestURL + "/updateNotification/" + questionStatusId + "/" + notify);
    }

    @Override
    public Set<Long> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return new HashSet(Arrays.asList(restGet(questionRestURL + "/getMarkedAsComplete/" + applicationId + "/" + organisationId, Long[].class)));
    }

    @Override
    public Question findById(Long questionId) {
        return restGet(questionRestURL + "/id/" + questionId, Question.class);
    }

    @Override
    public Question getNextQuestion(Long questionId) {
        return restGet(questionRestURL + "/getNextQuestion/"+questionId, Question.class);
    }

    @Override
    public Question getPreviousQuestion(Long questionId) {
        return restGet(questionRestURL + "/getPreviousQuestion/"+questionId, Question.class);
    }

    @Override
    public Question getPreviousQuestionBySection(Long sectionId) {
        return restGet(questionRestURL + "/getPreviousQuestionBySection/"+sectionId, Question.class);
    }

    @Override
    public Question getNextQuestionBySection(Long sectionId) {
        return restGet(questionRestURL + "/getNextQuestionBySection/"+sectionId, Question.class);
    }
}
