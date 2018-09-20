package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackNavigationViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.form.resource.SectionType.GENERAL;

/**
 * Build the model for Assessment Feedback navigation view.
 */
@Component
public class AssessmentFeedbackNavigationModelPopulator extends AssessmentModelPopulator<AssessmentFeedbackNavigationViewModel> {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Override
    public AssessmentFeedbackNavigationViewModel populate(final long assessmentId, final QuestionResource question) {
        return new AssessmentFeedbackNavigationViewModel(assessmentId, getPreviousQuestion(question.getId()), getNextQuestion(question.getId()));
    }

    private Optional<QuestionResource> getPreviousQuestion(final long questionId) {
        return getPreviousAllowedQuestion(questionId);
    }

    private Optional<QuestionResource> getNextQuestion(final long questionId) {
        return getNextAllowedQuestion(questionId);
    }

    private Optional<QuestionResource> getNextAllowedQuestion(long questionId) {
        Optional<QuestionResource> optionalQuestion = questionService.getNextQuestion(questionId);
        if (optionalQuestion.isPresent()) {
            QuestionResource question = optionalQuestion.get();
            if(isAssessmentQuestion(question)) {
                return Optional.of(question);
            }
            return getNextAllowedQuestion(question.getId());
        }
        return Optional.empty();
    }

    private Optional<QuestionResource> getPreviousAllowedQuestion(long questionId) {
        Optional<QuestionResource> optionalQuestion = questionService.getPreviousQuestion(questionId);
        if (optionalQuestion.isPresent()) {
            QuestionResource question = optionalQuestion.get();
            if(isAssessmentQuestion(question)) {
                return Optional.of(question);
            }
            return getPreviousQuestion(question.getId());
        }
        return Optional.empty();
    }

    private boolean isAssessmentQuestion(QuestionResource question) {
        return sectionService.getById(question.getSection()).getType() == GENERAL
                && (question.getQuestionSetupType() != APPLICATION_TEAM)
                && (question.getQuestionSetupType() != RESEARCH_CATEGORY);
    }
}
