package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * QuestionStatusController exposes question status data and operations through a REST API.
 * It is mainly used at present for getting question statuses for given question in given application.
 */
@RestController
@RequestMapping("/questionStatus")
public class QuestionStatusController {

    @Autowired
    QuestionStatusRepository questionStatusRepository;

    @Autowired
    QuestionStatusMapper questionStatusMapper;

    @RequestMapping("/findByQuestionAndApplication/{questionId}/{applicationId}")
    private List<QuestionStatus> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
            return questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
    }

    @RequestMapping("/findByQuestionAndApplicationAndOrganisation/{questionId}/{applicationId}/{organisationId}")
    private List<QuestionStatusResource> getQuestionStatusByApplicationIdAndAssigneeIdAndOrganisationId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeOrganisationId(questionId, applicationId, organisationId);
        return simpleMap(questionStatuses, questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{applicationId}/{organisationId}", params = "questionIds")
    private List<QuestionStatusResource> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@RequestParam List<Long> questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdIsInAndApplicationIdAndAssigneeOrganisationId(questionIds, applicationId, organisationId);
        return simpleMap(questionStatuses, questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    private List<QuestionStatusResource> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationIdAndAssigneeOrganisationId(applicationId, organisationId);
        return simpleMap(questionStatuses, questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping("/{id}")
    private QuestionStatus getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusRepository.findOne(id);
    }


}
