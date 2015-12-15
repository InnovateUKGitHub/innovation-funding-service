package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/questionStatus")
public class QuestionStatusController {

    @Autowired
    QuestionStatusRepository questionStatusRepository;

    @Autowired
    QuestionRepository questionRepository;

    @RequestMapping(value="/findByQuestionAndAplication/{questionId}/{applicationId}")
    private List<QuestionStatus> getQuestionStatusByApplicationIdAndAssigneeId(@PathVariable("questionId") Long questionId, @PathVariable("applicationId") Long applicationId) {
            return questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);
    }


}
