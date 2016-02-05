package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
        return simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping(value = "/findByQuestionIdsAndApplicationIdAndOrganisationId/{questionIds}/{applicationId}/{organisationId}")
    private List<QuestionStatusResource> getQuestionStatusByQuestionIdsAndApplicationIdAndOrganisationId(@PathVariable Long[] questionIds, @PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByQuestionIdIsInAndApplicationId(Arrays.asList(questionIds), applicationId);
        return simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping("/findByApplicationAndOrganisation/{applicationId}/{organisationId}")
    private List<QuestionStatusResource> findByApplicationAndOrganisation(@PathVariable("applicationId") Long applicationId, @PathVariable("organisationId") Long organisationId){
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(applicationId);
        return simpleMap(filterByOrganisationIdIfHasMultipleStatuses(questionStatuses, organisationId), questionStatusMapper :: mapQuestionStatusToPopulatedResource);
    }

    @RequestMapping("/{id}")
    private QuestionStatus getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusRepository.findOne(id);
    }

    private List<QuestionStatus> filterByOrganisationIdIfHasMultipleStatuses(final List<QuestionStatus> questionStatuses, Long organisationId) {
        return questionStatuses.stream().
                filter(qs -> (!qs.getQuestion().hasMultipleStatuses() || (qs.getAssignee() != null && qs.getAssignee().getOrganisation().getId().equals(organisationId))))
                .collect(Collectors.toList());
    }
}
