package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.transactional.QuestionAssessmentService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * QuestionController exposes question data and operations through a REST API.
 */
@RestController
@RequestMapping("/questionAssessment")
public class QuestionAssessmentController {

    @Autowired
    private QuestionAssessmentService questionAssesmentService;

    @RequestMapping("/{id}")
    public RestResult<QuestionAssessmentResource> getById(@PathVariable("id") final Long id) {
        return questionAssesmentService.getById(id).toGetResponse();
    }

    @RequestMapping("/findByQuestion/{id}")
    public RestResult<QuestionAssessmentResource> getByQuestionId(@PathVariable("id") final Long questionId) {
        return questionAssesmentService.findByQuestion(questionId).toGetResponse();
    }

}
