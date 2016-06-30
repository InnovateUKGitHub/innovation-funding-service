package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
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
    private AssessmentFeedbackService assessmentFeedbackService;
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value= "/{assessmentId}",
                    method = RequestMethod.GET)
    public String getSummary(
            @PathVariable("assessmentId") final Long assessmentId,
            final Model model) throws ExecutionException, InterruptedException {
            model.addAttribute("model", populateModel(assessmentId));
            return "assessor-application-summary";
    }

    private List<AssessmentSummaryViewModel> populateModel(Long assessmentId) throws ExecutionException, InterruptedException {
        List<AssessmentSummaryViewModel> listOfAssessmentSummaryViewModel = new ArrayList<>();
        assessmentFeedbackService.getAllAssessmentFeedback(assessmentId).stream().forEach(assessmentFeedbackResource -> {
            Long questionId = assessmentFeedbackResource.getQuestion();
            QuestionResource questionResource = questionService.getById(questionId);
            listOfAssessmentSummaryViewModel.add(new AssessmentSummaryViewModel(questionResource,assessmentFeedbackResource));
        });

        return listOfAssessmentSummaryViewModel;

    }


}
