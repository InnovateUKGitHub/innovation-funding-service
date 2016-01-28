package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.QuestionStatusRepository;
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
    QuestionStatusRepository questionStatusRepository;

    @RequestMapping("/findByQuestionAndApplication/{questionId}/{applicationId}")
    private List<QuestionStatus> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
            return questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
    }

    @RequestMapping("/{id}")
    private QuestionStatus getQuestionStatusResourceById(@PathVariable("id") Long id){
        return questionStatusRepository.findOne(id);
    }


}
