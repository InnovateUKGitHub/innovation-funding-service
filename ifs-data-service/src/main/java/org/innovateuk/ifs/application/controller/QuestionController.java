package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.resource.FormInputType;
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

    @GetMapping("/id/{id}")
    public RestResult<QuestionResource> getQuestionById(@PathVariable("id") final Long id) {
        return questionService.getQuestionById(id).toGetResponse();
    }

    @PutMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("questionId") final Long questionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.markAsComplete(ids, markedAsCompleteById).toPutWithBodyResponse();
    }

    @PutMapping("/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final Long questionId,
                                 @PathVariable("applicationId") final Long applicationId,
                                 @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.markAsInComplete(ids, markedAsInCompleteById).toPutResponse();
    }

    @PutMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final Long questionId,
                       @PathVariable("applicationId") final Long applicationId,
                       @PathVariable("assigneeId") final Long assigneeId,
                       @PathVariable("assignedById") final Long assignedById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionService.assign(ids, assigneeId, assignedById).toPutResponse();
    }

    @GetMapping("/getMarkedAsComplete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                         @PathVariable("organisationId") Long organisationId) {
        return questionService.getMarkedAsComplete(applicationId, organisationId).toGetResponse();
    }


    @PutMapping("/updateNotification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                   @PathVariable("notify") final Boolean notify) {
        return questionService.updateNotification(questionStatusId, notify).toPutResponse();
    }

    @GetMapping("/findByCompetition/{competitionId}")
    public RestResult<List<QuestionResource>> findByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return questionService.findByCompetition(competitionId).toGetResponse();
    }

    @GetMapping("/getNextQuestion/{questionId}")
    public RestResult<QuestionResource> getNextQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getNextQuestion(questionId).toGetResponse();
    }

    @GetMapping("/getPreviousQuestionBySection/{sectionId}")
    public RestResult<QuestionResource> getPreviousQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getPreviousQuestionBySection(sectionId).toGetResponse();
    }

    @GetMapping("/getNextQuestionBySection/{sectionId}")
    public RestResult<QuestionResource> getNextQuestionBySection(@PathVariable("sectionId") final Long sectionId) {
        return questionService.getNextQuestionBySection(sectionId).toGetResponse();
    }

    @GetMapping("/getPreviousQuestion/{questionId}")
    public RestResult<QuestionResource> getPreviousQuestion(@PathVariable("questionId") final Long questionId) {
        return questionService.getPreviousQuestion(questionId).toGetResponse();
    }

    @GetMapping("/getQuestionByCompetitionIdAndFormInputType/{competitionId}/{formInputType}")
    public RestResult<QuestionResource> getQuestionByFormInputType(@PathVariable("competitionId") final Long competitionId,
                                                                   @PathVariable("formInputType") final FormInputType formInputType) {
        return questionService.getQuestionResourceByCompetitionIdAndFormInputType(competitionId, formInputType).toGetResponse();
    }
    
    @GetMapping("/getQuestionsBySectionIdAndType/{sectionId}/{type}")
    public RestResult<List<QuestionResource>> getQuestionsBySectionIdAndType(@PathVariable("sectionId") final Long sectionId, @PathVariable("type") QuestionType type) {
    	return questionService.getQuestionsBySectionIdAndType(sectionId, type).toGetResponse();
    }
	
	@PutMapping("/")
    public RestResult<QuestionResource> save(@RequestBody final QuestionResource questionResource) {
        return questionService.save(questionResource).toGetResponse();
    }

    @GetMapping("/getQuestionByIdAndAssessmentId/{questionId}/{assessmentId}")
    public RestResult<QuestionResource> getQuestionByIdAndAssessmentId(@PathVariable("questionId") Long questionId, @PathVariable("assessmentId") Long assessmentId) {
        return questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).toGetResponse();
    }

    @GetMapping("/getQuestionsByAssessment/{assessmentId}")
    public RestResult<List<QuestionResource>> getQuestionsByAssessmentId(@PathVariable("assessmentId") final Long assessmentId) {
        return questionService.getQuestionsByAssessmentId(assessmentId).toGetResponse();
    }
}
