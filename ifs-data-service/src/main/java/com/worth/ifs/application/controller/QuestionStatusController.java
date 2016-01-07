package com.worth.ifs.application.controller;

import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/findByQuestionAndAplication/{questionId}/{applicationId}")
    private List<QuestionStatusResource> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
            return simpleMap(questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId), questionStatusMapper::mapQuestionStatusToResource);
    }

    @RequestMapping("/{id}")
    private QuestionStatusResource getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusMapper.mapQuestionStatusToResource(questionStatusRepository.findOne(id));
    }


}
