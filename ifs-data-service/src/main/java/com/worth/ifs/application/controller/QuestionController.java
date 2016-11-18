package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public RestResult<QuestionResource> getQuestionById(@PathVariable("id") final Long id) {
        return questionService.getQuestionById(id).toGetResponse();
    }

    @RequestMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("questionId") final Long questionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.markAsComplete(ids, markedAsCompleteById).toPutWithBodyResponse();
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
    public RestResult<List<QuestionResource>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId).toGetResponse();
    }

    @RequestMapping("/getNextQuestion/{questionId}")
    public RestResult<QuestionResource> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId).toGetResponse();
    }

    @RequestMapping("/getPreviousQuestionBySection/{sectionId}")
    public RestResult<QuestionResource> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId).toGetResponse();
    }

    @RequestMapping("/getNextQuestionBySection/{sectionId}")
    public RestResult<QuestionResource> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId).toGetResponse();
    }

    @RequestMapping("/getPreviousQuestion/{questionId}")
    public RestResult<QuestionResource> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId).toGetResponse();
    }

    @RequestMapping("/getQuestionByCompetitionIdAndFormInputType/{competitionId}/{formInputType}")
    public RestResult<QuestionResource> getQuestionByFormInputType(@PathVariable("competitionId") final Long competitionId,
                                                                   @PathVariable("formInputType") final String formInputType) {
        return questionService.getQuestionResourceByCompetitionIdAndFormInputType(competitionId, formInputType).toGetResponse();
    }
    
    @RequestMapping("/getQuestionsBySectionIdAndType/{sectionId}/{type}")
    public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(@PathVariable("sectionId") final Long sectionId, @PathVariable("type") QuestionType type) {
    	return questionService.getQuestionsBySectionIdAndType(sectionId, type).toGetResponse();
    }
	
	@RequestMapping(path = "/", method = RequestMethod.PUT)
    public RestResult<QuestionResource> save(@RequestBody final QuestionResource questionResource) {
        return questionService.save(questionResource).toGetResponse();
    }

    @RequestMapping("/getQuestionByIdAndAssessmentId/{questionId}/{assessmentId}")
    public RestResult<QuestionResource> getQuestionByIdAndAssessmentId(@PathVariable("questionId") Long questionId, @PathVariable("assessmentId") Long assessmentId) {
        return questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).toGetResponse();
    }

    @RequestMapping("/getQuestionsByAssessment/{assessmentId}")
    public RestResult<List<QuestionResource>> getQuestionsByAssessmentId(@PathVariable("assessmentId") final Long assessmentId) {
        return questionService.getQuestionsByAssessmentId(assessmentId).toGetResponse();
    }
}
