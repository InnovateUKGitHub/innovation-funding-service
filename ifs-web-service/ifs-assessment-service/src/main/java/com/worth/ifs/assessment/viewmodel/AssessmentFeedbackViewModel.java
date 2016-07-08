package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Holder of model attributes for the feedback given as part of the assessment journey to a question for an application.
 */
public class AssessmentFeedbackViewModel {

    private final long daysLeft;
    private final long daysLeftPercentage;
    private final CompetitionResource competition;
    private final ApplicationResource application;
    private final Long questionId;
    private final String questionNumber;
    private final String questionShortName;
    private final String questionName;
    private final String questionResponse;
    private final boolean requireScore;
    private final boolean requireFeedback;
    private final boolean requireCategory;
    private final boolean requireScopeConfirmation;
    private final String assessorGuidanceQuestion;
    private final String assessorGuidanceAnswer;
    private final boolean appendixExists;
    private final FileDetailsViewModel appendixDetails;

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, String questionResponse, boolean requireScore, boolean requireFeedback, boolean requireCategory, boolean requireScopeConfirmation, String assessorGuidanceQuestion, String assessorGuidanceAnswer) {
        this(daysLeft, daysLeftPercentage, competition, application, questionId, questionNumber, questionShortName, questionName, questionResponse, requireScore, requireFeedback, requireCategory, requireScopeConfirmation, assessorGuidanceQuestion, assessorGuidanceAnswer, false, null);
    }

    public AssessmentFeedbackViewModel(long daysLeft, long daysLeftPercentage, CompetitionResource competition, ApplicationResource application, Long questionId, String questionNumber, String questionShortName, String questionName, String questionResponse, boolean requireScore, boolean requireFeedback, boolean requireCategory, boolean requireScopeConfirmation, String assessorGuidanceQuestion, String assessorGuidanceAnswer, boolean appendixExists, FileDetailsViewModel appendixDetails) {
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competition = competition;
        this.application = application;
        this.questionId = questionId;
        this.questionNumber = questionNumber;
        this.questionShortName = questionShortName;
        this.questionName = questionName;
        this.questionResponse = questionResponse;
        this.requireScore = requireScore;
        this.requireFeedback = requireFeedback;
        this.requireCategory = requireCategory;
        this.requireScopeConfirmation = requireScopeConfirmation;
        this.assessorGuidanceQuestion = assessorGuidanceQuestion;
        this.assessorGuidanceAnswer = assessorGuidanceAnswer;
        this.appendixExists = appendixExists;
        this.appendixDetails = appendixDetails;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public String getQuestionResponse() {
        return questionResponse;
    }

    public boolean isRequireScore() {
        return requireScore;
    }

    public boolean isRequireFeedback() {
        return requireFeedback;
    }

    public boolean isRequireCategory() {
        return requireCategory;
    }

    public boolean isRequireScopeConfirmation() {
        return requireScopeConfirmation;
    }

    public String getAssessorGuidanceQuestion() {
        return assessorGuidanceQuestion;
    }

    public String getAssessorGuidanceAnswer() {
        return assessorGuidanceAnswer;
    }

    public boolean isAppendixExists() {
        return appendixExists;
    }

    public FileDetailsViewModel getAppendixDetails() {
        return appendixDetails;
    }

    public String getAppendixFileDescription() {
        return format("View %s appendix", lowerCase(getQuestionShortName()));
    }
}
