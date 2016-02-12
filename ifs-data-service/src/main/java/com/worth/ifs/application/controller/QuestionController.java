package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


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
        return questionService.getQuestionById(id).toDefaultRestResultForGet();
    }

    @RequestMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<Void> markAsComplete(@PathVariable("questionId") final Long questionId,
                        @PathVariable("applicationId") final Long applicationId,
                        @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return questionService.markAsComplete(questionId, applicationId, markedAsCompleteById).toDefaultRestResultForPut();
    }

    @RequestMapping("/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        return questionService.markAsInComplete(questionId, applicationId, markedAsInCompleteById).toDefaultRestResultForPut();
    }

    @RequestMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        return questionService.assign(questionId, applicationId, assigneeId, assignedById).toDefaultRestResultForPut();
    }

    @RequestMapping("/getMarkedAsComplete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
        return questionService.getMarkedAsComplete(applicationId, organisationId).toDefaultRestResultForGet();
    }


    @RequestMapping("/updateNotification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                   @PathVariable("notify") final Boolean notify) {
        return questionService.updateNotification(questionStatusId, notify).toDefaultRestResultForPut();
    }

    @RequestMapping("/findByCompetition/{competitionId}")
    public RestResult<List<Question>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getNextQuestion/{questionId}")
    public RestResult<Question> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getPreviousQuestionBySection/{sectionId}")
    public RestResult<Question> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId).toDefaultRestResultForGet();
    }

    @RequestMapping(value = "/getNextQuestionBySection/{sectionId}")
    public RestResult<Question> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getPreviousQuestion/{questionId}")
    public RestResult<Question> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId).toDefaultRestResultForGet();
    }
}
