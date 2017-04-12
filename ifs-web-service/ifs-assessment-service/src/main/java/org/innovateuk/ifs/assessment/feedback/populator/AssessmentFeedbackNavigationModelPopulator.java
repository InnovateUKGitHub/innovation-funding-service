package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentNavigationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.application.resource.SectionType.GENERAL;

/**
 * Build the model for Assessment Feedback navigation view.
 */
@Component
public class AssessmentFeedbackNavigationModelPopulator {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    public AssessmentNavigationViewModel populateModel(final Long assessmentId, final Long questionId) {
        return new AssessmentNavigationViewModel(assessmentId, getPreviousQuestion(questionId), getNextQuestion(questionId));
    }

    private Optional<QuestionResource> getPreviousQuestion(final Long questionId) {
        return questionService.getPreviousQuestion(questionId);
    }

    private Optional<QuestionResource> getNextQuestion(final Long questionId) {
        return questionService.getNextQuestion(questionId).filter(this::isAssessmentQuestion);
    }

    private boolean isAssessmentQuestion(QuestionResource question) {
        return sectionService.getById(question.getSection()).getType() == GENERAL;
    }
}
