package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
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
    public RestResult<List<QuestionStatus>> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
        return questionService.getQuestionStatusByApplicationIdAndAssigneeId(questionId, applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId) {
        return questionService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId, applicationId, organisationId).toDefaultRestResultForGet();
    }

    @RequestMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.findByApplicationAndOrganisation(applicationId, organisationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/{id}")
    public RestResult<QuestionStatus> getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionService.getQuestionStatusResourceById(id).toDefaultRestResultForGet();
    }
}
