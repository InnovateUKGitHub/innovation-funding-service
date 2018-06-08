package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * QuestionStatusController exposes question status data and operations through a REST API.
 * It is mainly used at present for getting question statuses for given question in given application.
 */
@RestController
@RequestMapping("/questionStatus")
public class QuestionStatusController {

    @Autowired
    private QuestionStatusService questionStatusService;

    @GetMapping("/findByQuestionAndApplication/{questionId}/{applicationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(@PathVariable("questionId") long questionId, @PathVariable("applicationId") long applicationId) {
        return questionStatusService.getQuestionStatusByQuestionIdAndApplicationId(questionId, applicationId).toGetResponse();
    }

    @GetMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") long questionId, @PathVariable("applicationId") long applicationId, @PathVariable("organisationId") Long organisationId) {
        return questionStatusService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId, applicationId, organisationId).toGetResponse();
    }

    @GetMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") long applicationId, @PathVariable("organisationId") long organisationId){
        return questionStatusService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(@PathVariable("applicationId") long applicationId, @PathVariable("organisationId") long organisationId){
        return questionStatusService.findByApplicationAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/{id}")
    public RestResult<QuestionStatusResource> getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusService.getQuestionStatusResourceById(id).toGetResponse();
    }

    @GetMapping("/getAssignedQuestionsCountByApplicationIdAndAssigneeId/{applicationId}/{assigneeId}")
    public RestResult<Integer> getAssignedQuestionsCountByApplicationIdAndAssigneeId(@PathVariable("applicationId") final long applicationId,
                                                                                     @PathVariable("assigneeId") final long assigneeId) {
        return questionStatusService.getCountByApplicationIdAndAssigneeId(applicationId, assigneeId).toGetResponse();
    }

    @PutMapping("/mark-as-complete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("questionId") final long questionId,
                                                               @PathVariable("applicationId") final long applicationId,
                                                               @PathVariable("markedAsCompleteById") final long markedAsCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.markAsComplete(ids, markedAsCompleteById).toPutWithBodyResponse();
    }

    @PutMapping("/mark-as-in-complete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final long questionId,
                                             @PathVariable("applicationId") final long applicationId,
                                             @PathVariable("markedAsInCompleteById") final long markedAsInCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.markAsInComplete(ids, markedAsInCompleteById).toPutResponse();
    }

    @PutMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final long questionId,
                                   @PathVariable("applicationId") final long applicationId,
                                   @PathVariable("assigneeId") final long assigneeId,
                                   @PathVariable("assignedById") final long assignedById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.assign(ids, assigneeId, assignedById).toPutResponse();
    }

    @GetMapping("/get-marked-as-complete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") final long applicationId,
                                                     @PathVariable("organisationId") final long organisationId) {
        return questionStatusService.getMarkedAsComplete(applicationId, organisationId).toGetResponse();
    }

    @PutMapping("/update-notification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final long questionStatusId,
                                               @PathVariable("notify") final boolean notify) {
        return questionStatusService.updateNotification(questionStatusId, notify).toPutResponse();
    }

}
