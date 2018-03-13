package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
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
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
        return questionStatusService.getQuestionStatusByQuestionIdAndApplicationId(questionId, applicationId).toGetResponse();
    }

    @GetMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId) {
        return questionStatusService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId, applicationId, organisationId).toGetResponse();
    }

    @GetMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionStatusService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionStatusService.findByApplicationAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/{id}")
    public RestResult<QuestionStatusResource> getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusService.getQuestionStatusResourceById(id).toGetResponse();
    }

    @GetMapping("/getAssignedQuestionsCountByApplicationIdAndAssigneeId/{applicationId}/{assigneeId}")
    public RestResult<Integer> getAssignedQuestionsCountByApplicationIdAndAssigneeId(@PathVariable("applicationId") final Long applicationId,
                                                                                     @PathVariable("assigneeId") final Long assigneeId) {
        return questionStatusService.getCountByApplicationIdAndAssigneeId(applicationId, assigneeId).toGetResponse();
    }

    @PutMapping("/markAsComplete/{questionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("questionId") final Long questionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.markAsComplete(ids, markedAsCompleteById).toPutWithBodyResponse();
    }

    @PutMapping("/markAsInComplete/{questionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("questionId") final Long questionId,
                                             @PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.markAsInComplete(ids, markedAsInCompleteById).toPutResponse();
    }

    @PutMapping("/assign/{questionId}/{applicationId}/{assigneeId}/{assignedById}")
    public RestResult<Void> assign(@PathVariable("questionId") final Long questionId,
                                   @PathVariable("applicationId") final Long applicationId,
                                   @PathVariable("assigneeId") final Long assigneeId,
                                   @PathVariable("assignedById") final Long assignedById) {
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionId, applicationId);
        return questionStatusService.assign(ids, assigneeId, assignedById).toPutResponse();
    }

    @GetMapping("/getMarkedAsComplete/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getMarkedAsComplete(@PathVariable("applicationId") Long applicationId,
                                                     @PathVariable("organisationId") Long organisationId) {
        return questionStatusService.getMarkedAsComplete(applicationId, organisationId).toGetResponse();
    }

    @PutMapping("/updateNotification/{questionStatusId}/{notify}")
    public RestResult<Void> updateNotification(@PathVariable("questionStatusId") final Long questionStatusId,
                                               @PathVariable("notify") final Boolean notify) {
        return questionStatusService.updateNotification(questionStatusId, notify).toPutResponse();
    }

}
