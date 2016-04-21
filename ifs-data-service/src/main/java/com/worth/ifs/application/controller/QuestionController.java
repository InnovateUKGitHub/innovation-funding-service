package com.worth.ifs.application.controller;

import java.util.List;
import java.util.Set;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.rest.RestResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/id/{id}")
    public RestResult<Question> getQuestionById(@PathVariable("id") final Long id) {
        return questionService.getQuestionById(id).toGetResponse();
    }

    @RequestMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<Void> markAsComplete(@PathVariable("questionId") final Long questionId,
                        @PathVariable("applicationId") final Long applicationId,
                        @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.markAsComplete(ids, markedAsCompleteById).toPutResponse();
    }

    @RequestMapping("/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.markAsInComplete(ids, markedAsInCompleteById).toPutResponse();
    }

    @RequestMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.assign(ids, assigneeId, assignedById).toPutResponse();
    }

    @RequestMapping("/getMarkedAsComplete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
        return questionService.getMarkedAsComplete(applicationId, organisationId).toGetResponse();
    }


    @RequestMapping("/updateNotification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                   @PathVariable("notify") final Boolean notify) {
        return questionService.updateNotification(questionStatusId, notify).toPutResponse();
    }

    @RequestMapping("/findByCompetition/{competitionId}")
    public RestResult<List<Question>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId).toGetResponse();
    }

    @RequestMapping("/getNextQuestion/{questionId}")
    public RestResult<Question> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId).toGetResponse();
    }

    @RequestMapping("/getPreviousQuestionBySection/{sectionId}")
    public RestResult<Question> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId).toGetResponse();
    }

    @RequestMapping("/getNextQuestionBySection/{sectionId}")
    public RestResult<Question> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId).toGetResponse();
    }

    @RequestMapping("/getPreviousQuestion/{questionId}")
    public RestResult<Question> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId).toGetResponse();
    }

    @RequestMapping("/getQuestionByFormInputType/{formInputType}")
    public RestResult<Question> getQuestionByFormInputType(@PathVariable("formInputType") final String formInputType) {
        return questionService.getQuestionByFormInputType(formInputType).toGetResponse();
    }
}
