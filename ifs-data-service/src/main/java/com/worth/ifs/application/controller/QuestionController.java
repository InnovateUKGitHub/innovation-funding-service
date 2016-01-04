package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    QuestionService questionService;


    @RequestMapping("/id/{id}")
    public Question getQuestionById(@PathVariable("id") final Long id) {
        return questionService.getQuestionById(id);
    }

    @RequestMapping(value = "/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public void markAsComplete(@PathVariable("questionId") final Long questionId,
                               @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        questionService.markAsComplete(questionId, applicationId, markedAsCompleteById);
    }

    @RequestMapping(value = "/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public void markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        questionService.markAsInComplete(questionId, applicationId, markedAsInCompleteById);
    }

    @RequestMapping(value = "/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public void assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        questionService.assign(questionId, applicationId, assigneeId, assignedById);
    }

    @RequestMapping(value="/getMarkedAsComplete/{applicationId}/{organisationId}")
    public Set<Long> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
        return questionService.getMarkedAsComplete(applicationId, organisationId);
    }


    @RequestMapping(value = "/updateNotification/{questionStatusId}/{notify}")
    public void updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                   @PathVariable("notify") final Boolean notify) {
        questionService.updateNotification(questionStatusId, notify);
    }

    @RequestMapping(value = "/findByCompetition/{competitionId}")
    public List<Question> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId);
    }

    @RequestMapping(value = "/getNextQuestion/{questionId}")
    public Question getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId);
    }

    @RequestMapping(value = "/getPreviousQuestionBySection/{sectionId}")
    public Question getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId);
    }

    @RequestMapping(value = "/getNextQuestionBySection/{sectionId}")
    public Question getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId);
    }

    @RequestMapping(value = "/getPreviousQuestion/{questionId}")
    public Question getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId);
    }
}
