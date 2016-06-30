package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Exposes CRUD operations through a REST API to manage assessment related web data operations.
 */
@Controller
@RequestMapping("/assessment/summary")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @RequestMapping(value= "/{assessmentId}",
                    method = RequestMethod.GET)
    public String getAllQuestionsOfGivenAssessment(
            @PathVariable("assessmentId") final Long assessmentId,
            final Model model) throws ExecutionException, InterruptedException {

           List<AssessmentFeedbackResource> listOfAssessmentFeedback = new ArrayList<>();
           assessmentService.getAllQuestionsById(assessmentId).stream().forEach(questionResource -> {
               Long questionId = questionResource.getId();
               listOfAssessmentFeedback.add(assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId,questionId));
           });
           model.addAttribute("model", populateModel(listOfAssessmentFeedback));
           return "assessor-application-summary";
    }

    private AssessmentSummaryViewModel populateModel(List<AssessmentFeedbackResource> listOfAssessmentFeedback) {
        return new AssessmentSummaryViewModel(listOfAssessmentFeedback);

    }


}
