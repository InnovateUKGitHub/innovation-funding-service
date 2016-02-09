package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.transactional.QuestionService;
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
    public List<QuestionStatus> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
        return questionService.getQuestionStatusByApplicationIdAndAssigneeId(questionId, applicationId);
    }

    @RequestMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    public List<QuestionStatusResource> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId) {
        return questionService.getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(questionId, applicationId, organisationId);
    }

    @RequestMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    public List<QuestionStatusResource> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, organisationId);
    }

    @RequestMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    public List<QuestionStatusResource> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        return questionService.findByApplicationAndOrganisation(applicationId, organisationId);
    }

    @RequestMapping("/{id}")
    public QuestionStatus getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionService.getQuestionStatusResourceById(id);
    }
}
