package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;
import java.util.Map;

/**
 * Holder of model attributes for the feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackViewModel {

    private final CompetitionResource competition;
    private final QuestionResource question;
    private final List<FormInputResource> questionFormInputs;
    private final Map<String, String> questionFormInputResponses;

    public AssessmentFeedbackViewModel(CompetitionResource competition, QuestionResource question, List<FormInputResource> questionFormInputs, Map<String, String> questionFormInputResponses) {
        this.competition = competition;
        this.question = question;
        this.questionFormInputs = questionFormInputs;
        this.questionFormInputResponses = questionFormInputResponses;
    }

    public long getDaysLeftPercentage() {
        return competition.getAssessmentDaysLeftPercentage();
    }

    public long getDaysLeft() {
        return competition.getAssessmentDaysLeft();
    }

    public String getTitle() {
        return question.getShortName();
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public List<FormInputResource> getQuestionFormInputs() {
        return questionFormInputs;
    }

    public Map<String, String> getQuestionFormInputResponses() {
        return questionFormInputResponses;
    }
}
