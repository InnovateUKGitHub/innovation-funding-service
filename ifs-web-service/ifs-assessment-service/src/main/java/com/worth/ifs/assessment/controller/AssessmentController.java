package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Exposes CRUD operations through a REST API to manage assessment related web data operations.
 */
@RestController
@RequestMapping("/assessment/summary")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @RequestMapping(value= "/{id}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssessmentFeedbackResource>> getAllQuestionsOfGivenAssessment(
            @PathVariable("id") final Long assessmentId,
            final Model model) throws ExecutionException, InterruptedException {
           List<AssessmentFeedbackResource> listOfAssessmentFeedback = new ArrayList<>();
           assessmentService.getAllQuestionsById(assessmentId).stream().forEach(questionResource -> {
               Long questionId = questionResource.getId();
               listOfAssessmentFeedback.add(assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId,questionId));
           });
        return new ResponseEntity<>(listOfAssessmentFeedback, HttpStatus.OK);
    }

}
