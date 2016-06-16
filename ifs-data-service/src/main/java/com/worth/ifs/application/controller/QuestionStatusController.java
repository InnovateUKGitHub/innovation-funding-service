package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * QuestionStatusController exposes question status data and operations through a REST API.
 * It is mainly used at present for getting question statuses for given question in given application.
 */
@RestController
@RequestMapping("/questionStatus")
public class QuestionStatusController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/findByQuestionAndApplication/{questionId}/{applicationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdAndApplicationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
        return questionService.getQuestionStatusByQuestionIdAndApplicationId(questionId, applicationId).toGetResponse();
    }

    @RequestMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId) {
        return questionService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId, applicationId, organisationId).toGetResponse();
    }

    @RequestMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.findByApplicationAndOrganisation(applicationId, organisationId).toGetResponse();
    }

    @RequestMapping("/{id}")
    public RestResult<QuestionStatusResource> getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionService.getQuestionStatusResourceById(id).toGetResponse();
    }

    @RequestMapping("/getAssignedQuestionsCountByApplicationIdAndAssigneeId/{applicationId}/{assigneeId}")
    public RestResult<Integer> getAssignedQuestionsCountByApplicationIdAndAssigneeId(@PathVariable("applicationId") final Long applicationId,
                                                                                     @PathVariable("assigneeId") final Long assigneeId) {

        return questionService.getCountByApplicationIdAndAssigneeId(applicationId, assigneeId).toGetResponse();
    }
}
