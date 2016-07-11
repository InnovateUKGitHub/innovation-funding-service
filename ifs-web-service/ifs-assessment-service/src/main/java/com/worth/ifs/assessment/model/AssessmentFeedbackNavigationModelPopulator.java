package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Build the model for Assessment Feedback navigation view.
 */
@Component
public class AssessmentFeedbackNavigationModelPopulator {

    @Autowired
    private QuestionService questionService;

    public AssessmentNavigationViewModel populateModel(final Long assessmentId, final Long questionId) {
        return new AssessmentNavigationViewModel(assessmentId, getPreviousQuestion(questionId), getNextQuestion(questionId));
    }

    private Optional<QuestionResource> getPreviousQuestion(final Long questionId) {
        return questionService.getPreviousQuestion(questionId);
    }

    private Optional<QuestionResource> getNextQuestion(final Long questionId) {
        return questionService.getNextQuestion(questionId);
    }

}
