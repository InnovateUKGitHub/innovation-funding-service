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

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;


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
        return newRestHandler().perform(() -> questionService.getQuestionById(id));
    }

    @RequestMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<Void> markAsComplete(@PathVariable("questionId") final Long questionId,
                        @PathVariable("applicationId") final Long applicationId,
                        @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return newRestHandler().perform(() -> questionService.markAsComplete(questionId, applicationId, markedAsCompleteById));
    }

    @RequestMapping("/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        return newRestHandler().perform(() -> questionService.markAsInComplete(questionId, applicationId, markedAsInCompleteById));
    }

    @RequestMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        return newRestHandler().perform(() -> questionService.assign(questionId, applicationId, assigneeId, assignedById));
    }

    @RequestMapping("/getMarkedAsComplete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
        return newRestHandler().perform(() -> questionService.getMarkedAsComplete(applicationId, organisationId));
    }


    @RequestMapping("/updateNotification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                   @PathVariable("notify") final Boolean notify) {
        return newRestHandler().perform(() -> questionService.updateNotification(questionStatusId, notify));
    }

    @RequestMapping("/findByCompetition/{competitionId}")
    public RestResult<List<Question>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return newRestHandler().perform(() -> questionService.findByCompetition(competitionId));
    }

    @RequestMapping("/getNextQuestion/{questionId}")
    public RestResult<Question> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return newRestHandler().perform(() -> questionService.getNextQuestion(questionId));
    }

    @RequestMapping("/getPreviousQuestionBySection/{sectionId}")
    public RestResult<Question> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return newRestHandler().perform(() -> questionService.getPreviousQuestionBySection(sectionId));
    }

    @RequestMapping(value = "/getNextQuestionBySection/{sectionId}")
    public RestResult<Question> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return newRestHandler().perform(() -> questionService.getNextQuestionBySection(sectionId));
    }

    @RequestMapping("/getPreviousQuestion/{questionId}")
    public RestResult<Question> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return newRestHandler().perform(() -> questionService.getPreviousQuestion(questionId));
    }
}
